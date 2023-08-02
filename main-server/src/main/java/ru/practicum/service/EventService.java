package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.exception.*;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.category.Category;
import ru.practicum.model.enums.SortBy;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.Status;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.NewEventDto;
import ru.practicum.model.event.dto.UpdateEventDto;
import ru.practicum.model.hit.dto.ViewHitStatsDto;
import ru.practicum.model.location.Location;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.model.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.model.user.User;
import ru.practicum.repository.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EventService {

    private static final Integer HOURS_BEFORE_EVENT = 2;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    public List<EventShortDto> getUserEvents(Integer userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        int page = from > 0 ? from / size : 0;
        Pageable eventsPageable = PageRequest.of(page, size);
        List<Event> events = eventRepository.findByInitiatorId(userId, eventsPageable);
        return eventMapper.listEventsToListEventShortDto(events);
    }

    //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента
    public EventFullDto createNewEvent(Integer userId, NewEventDto eventDto) {
        User initiator = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Category category = categoryRepository.findById(eventDto.getCategory())
                .orElseThrow(() -> new CategoryNotFoundException(eventDto.getCategory()));
        if (!validateDateTime(eventDto.getEventDate())) {
            throw new EventTooEarlyException(HOURS_BEFORE_EVENT);
        }
        Location location = locationRepository.save(eventDto.getLocation());
        Event event = eventMapper.newEventToEvent(eventDto);
        event.setLocation(location);
        event.setCategory(category);
        event.setCreatedOn(LocalDateTime.now());
        event.setInitiator(initiator);
        event.setPaid(eventDto.getPaid() != null ? eventDto.getPaid() : false);
        event.setParticipantLimit(eventDto.getParticipantLimit() != null ? eventDto.getParticipantLimit() : 0);
        event.setRequestModeration(eventDto.getRequestModeration() != null ? eventDto.getRequestModeration() : true);
        event.setState(State.PENDING);
        event.setViews(0);
        event.setConfirmedRequests(0);
        event = eventRepository.save(event);
        return eventMapper.eventToEventFullDto(event);
    }

    //В случае, если события с заданным id не найдено, возвращает статус код 404
    public EventFullDto getUserEvent(Integer userId, Integer eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        Event event = eventRepository.findByInitiatorIdAndId(userId, eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        return eventMapper.eventToEventFullDto(event);
    }

    //изменить можно только отмененные события или события в состоянии ожидания модерации (Ожидается код ошибки 409)
    //дата и время на которые намечено событие не может быть раньше, чем через два часа от текущего момента (Ожидается код ошибки 409)
    public EventFullDto updateEvent(Integer userId, Integer eventId, UpdateEventDto eventDto) {

        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        Event eventToUpdate = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (!eventToUpdate.getState().equals(State.PENDING) && !eventToUpdate.getState().equals(State.CANCELED)) {
            throw new InvalidEventStateException(eventToUpdate.getState());
        }
        if (eventDto.getEventDate() != null) {
            if (!validateDateTime(eventDto.getEventDate())) {
                throw new EventTooEarlyException(HOURS_BEFORE_EVENT);
            }
        }
        Category categoryToUpdate = eventToUpdate.getCategory();
        if (eventDto.getCategory() != null) {
            categoryToUpdate = categoryRepository
                    .findById(eventDto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(eventDto.getCategory()));
        }
        Event event = updateEventFields(eventToUpdate, eventDto, categoryToUpdate);
        event = eventRepository.save(event);
        return eventMapper.eventToEventFullDto(event);
    }

    public List<ParticipationRequestDto> getEventRequestsForUser(Integer userId, Integer eventId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(eventId);
        }
        return requestRepository.findRequestsForUserInEvent(userId, eventId);
    }


    //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
    public EventRequestStatusUpdateDto updateRequestStatus(Integer userId,
                                                           Integer eventId,
                                                           EventRequestStatusUpdateRequest statusUpdateDto) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        List<Request> requests = requestRepository.findByIdInAndEventInitiatorIdAndEventId(statusUpdateDto.getRequestIds(), userId, eventId);
        //статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
        //проверяем, что все в состоянии ожидания
        for (Request request : requests) {
            if (!(request.getStatus().equals(Status.PENDING))) {
                throw new InvalidRequestStateException(request.getStatus());
            }
        }
        //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
        //Т.е хотят подтвердить, а у нас уже достигнут лимит, значит выбрасываем исключение
        if (statusUpdateDto.getStatus().equals(Status.CONFIRMED)
                && event.getParticipantLimit() != 0
                && event.getConfirmedRequests().equals(event.getParticipantLimit())) {
            throw new MaxRequestsReachedException(event.getParticipantLimit());
        }
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();
        //когда убедились, что все в состоянии ожидания и лимит не достигнут, начинаем обработку
        for (Request request : requests) {
            if (statusUpdateDto.getStatus().equals(Status.REJECTED)) {
                request.setStatus(Status.REJECTED);
                requestRepository.save(request);
                rejectedRequests.add(requestMapper.requestToParticipationRequestDto(request));
            }
            if (statusUpdateDto.getStatus().equals(Status.CONFIRMED)) {
                //если при подтверждении данной заявки, лимит заявок для события исчерпан, то все неподтверждённые заявки необходимо отклонить
                if (!(event.getParticipantLimit() != 0 && event.getConfirmedRequests().equals(event.getParticipantLimit()))) {
                    request.setStatus(Status.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    eventRepository.save(event);
                    requestRepository.save(request);
                    confirmedRequests.add(requestMapper.requestToParticipationRequestDto(request));
                } else {
                    request.setStatus(Status.REJECTED);
                    requestRepository.save(request);
                    rejectedRequests.add(requestMapper.requestToParticipationRequestDto(request));
                }
            }
        }
        return new EventRequestStatusUpdateDto(confirmedRequests, rejectedRequests);
    }

    public List<EventFullDto> getEventsByAdmin(List<Integer> users,
                                               List<State> states,
                                               List<Integer> categories,
                                               LocalDateTime rangeStart,
                                               LocalDateTime rangeEnd,
                                               Integer from,
                                               Integer size) {
        List<EventFullDto> result;
        int page = from > 0 ? from / size : 0;
        Pageable eventsPageable = PageRequest.of(page, size);
        if (rangeStart == null || rangeEnd == null) {
            result = eventMapper.listEventToListEventFullDto(eventRepository.findEventsByUsersStatesCategoriesPageable(users, states, categories, eventsPageable));
        } else {
            result = eventMapper.listEventToListEventFullDto(eventRepository.findEventsByUsersStatesCategoriesDatePageable(users, states, categories, rangeStart, rangeEnd, eventsPageable));
        }
        return result;
    }

    public EventFullDto updateEventByAdmin(Integer eventId, UpdateEventDto updateEventAdminDto) {
        Event eventToUpdate = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (eventToUpdate.getEventDate() != null && eventToUpdate.getEventDate().isBefore(LocalDateTime.now().plusHours(1))) {
            throw new EventTooEarlyException(1);
        }
        if (updateEventAdminDto.getStateAction() != null) {
            if (updateEventAdminDto.getStateAction().equals(State.PUBLISH_EVENT)) {
                //событие можно публиковать, только если оно в состоянии ожидания публикации (Ожидается код ошибки 409)
                if (!eventToUpdate.getState().equals(State.PENDING)) {
                    throw new InvalidEventStateException(eventToUpdate.getState());
                }
                eventToUpdate.setPublishedOn(LocalDateTime.now());
                eventToUpdate.setState(State.PUBLISHED);
            }
            if (updateEventAdminDto.getStateAction().equals(State.REJECT_EVENT)) {
                if (eventToUpdate.getState().equals(State.PUBLISHED)) {
                    throw new InvalidEventStateException(eventToUpdate.getState());
                }
                eventToUpdate.setState(State.CANCELED);
            }
        }
        Category categoryToUpdate = null;
        if (updateEventAdminDto.getCategory() != null) {
            categoryToUpdate = categoryRepository.findById(updateEventAdminDto.getCategory())
                    .orElseThrow(() -> new CategoryNotFoundException(updateEventAdminDto.getCategory()));
        }
        eventToUpdate = updateEventFields(eventToUpdate, updateEventAdminDto, categoryToUpdate);
        return eventMapper.eventToEventFullDto(eventRepository.save(eventToUpdate));
    }

    //событие должно быть опубликовано
    //информация о событии должна включать в себя количество просмотров и количество подтвержденных запросов
    public EventFullDto getEvent(Integer eventId, String uri) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new EventNotFoundException(eventId);
        }
        List<ViewHitStatsDto> statsDtos = statsClient.getStats(LocalDateTime.of(1900, 1, 1, 0, 0), LocalDateTime.now(), new String[]{uri}, true);
        if (!statsDtos.isEmpty()) {
            event.setViews(statsDtos.get(0).getHits().intValue());
        } else {
            event.setViews(0);
        }

        return eventMapper.eventToEventFullDto(event);
    }

    //это публичный эндпоинт, соответственно в выдаче должны быть только опубликованные события
    //текстовый поиск (по аннотации и подробному описанию) должен быть без учета регистра букв
    //если в запросе не указан диапазон дат [rangeStart-rangeEnd], то нужно выгружать события, которые произойдут позже текущей даты и времени
    //информация о каждом событии должна включать в себя количество просмотров и количество уже одобренных заявок на участие
    //информацию о том, что по этому эндпоинту был осуществлен и обработан запрос, нужно сохранить в сервисе статистики
    public List<EventShortDto> getEvents(String text,
                                         List<Integer> categories,
                                         Boolean paid,
                                         LocalDateTime rangeStart,
                                         LocalDateTime rangeEnd,
                                         Boolean onlyAvailable,
                                         SortBy sort,
                                         Integer from,
                                         Integer size) {
        List<Event> result;
        Sort sortBy = Sort.by("views");
        int page = from > 0 ? from / size : 0;
        if (sort != null) {
            if (sort.equals(SortBy.EVENT_DATE)) {
                sortBy = Sort.by("eventDate");
            }
        }
        Pageable eventsPageable = PageRequest.of(page, size, sortBy);
        if (rangeStart != null && rangeEnd != null) {
            if (rangeEnd.isBefore(rangeStart)) {
                throw new BadRequestException("Дата окончания не может быть раньше даты начала");
            }
        }
        if (onlyAvailable) {
            if (rangeStart == null || rangeEnd == null) {
                result = eventRepository.findAvailablePublishedEvents(text, categories, paid, eventsPageable);
            } else {
                result = eventRepository.findAvailablePublishedWithDateEvents(text, categories, paid, rangeStart, rangeEnd, eventsPageable);
            }
        } else {
            if (rangeStart == null || rangeEnd == null) {
                result = eventRepository.findPublishedEvents(text, categories, paid, eventsPageable);
            } else {
                result = eventRepository.findPublishedWithDateEvents(text, categories, paid, rangeStart, rangeEnd, eventsPageable);
            }
        }
        return eventMapper.listEventsToListEventShortDto(result);
    }

    private Boolean validateDateTime(LocalDateTime dateTime) {
        return dateTime.isAfter(LocalDateTime.now().plusHours(HOURS_BEFORE_EVENT));
    }


    private Event updateEventFields(Event eventToUpdate, UpdateEventDto eventDto, Category category) {
        eventToUpdate.setAnnotation(eventDto.getAnnotation() == null ? eventToUpdate.getAnnotation() : eventDto.getAnnotation());
        eventToUpdate.setCategory(eventDto.getCategory() == null ? eventToUpdate.getCategory() : category);
        eventToUpdate.setDescription(eventDto.getDescription() == null ? eventToUpdate.getDescription() : eventDto.getDescription());
        eventToUpdate.setEventDate(eventDto.getEventDate() == null ? eventToUpdate.getEventDate() : eventDto.getEventDate());
        eventToUpdate.setLocation(eventDto.getLocation() == null ? eventToUpdate.getLocation() : eventDto.getLocation());
        eventToUpdate.setPaid(eventDto.getPaid() == null ? eventToUpdate.getPaid() : eventDto.getPaid());
        eventToUpdate.setParticipantLimit(eventDto.getParticipantLimit() == null ? eventToUpdate.getParticipantLimit() : eventDto.getParticipantLimit());
        eventToUpdate.setRequestModeration(eventDto.getRequestModeration() == null ? eventToUpdate.getRequestModeration() : eventDto.getRequestModeration());
        if (!(eventDto.getStateAction() == null)) {
            if (eventDto.getStateAction().equals(State.PUBLISH_EVENT)) {
                eventToUpdate.setState(State.PUBLISHED);
            }
            if (eventDto.getStateAction().equals(State.REJECT_EVENT) || eventDto.getStateAction().equals(State.CANCEL_REVIEW)) {
                eventToUpdate.setState(State.CANCELED);
            }
            if (eventDto.getStateAction().equals(State.SEND_TO_REVIEW)) {
                eventToUpdate.setState(State.PENDING);
            }
        }
        eventToUpdate.setTitle(eventDto.getTitle() == null ? eventToUpdate.getTitle() : eventDto.getTitle());
        return eventToUpdate;
    }


}