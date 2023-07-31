package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.exception.*;
import ru.practicum.mapper.RequestMapper;
import ru.practicum.model.enums.State;
import ru.practicum.model.enums.Status;
import ru.practicum.model.event.Event;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.model.user.User;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.RequestRepository;
import ru.practicum.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestService {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;
    private final EventRepository eventRepository;

    public List<ParticipationRequestDto> getUserRequests(Integer userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return requestMapper.listRequestsToListParticipationRequestDto(requestRepository.findByRequesterId(userId));
    }

    public ParticipationRequestDto createRequest(Integer userId, Integer eventId) {
        Request request = new Request();
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EventNotFoundException(eventId));
        //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (event.getInitiator().getId().equals(userId)) {
            throw new SelfParticipationException(eventId);
        }
        //нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new InvalidEventStateException(State.PUBLISHED, event.getState());
        }
        //нельзя добавить повторный запрос (Ожидается код ошибки 409)
        if (requestRepository.existsByRequesterIdAndEventId(userId, eventId)) {
            throw new RequestAlreadyExistException(eventId);
        }
        //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        if (event.getParticipantLimit() != 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new MaxRequestsReachedException(event.getParticipantLimit());
        }
        //если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(Status.PENDING);
        } else {
            request.setStatus(Status.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
        }
        request.setEvent(event);
        request.setRequester(user);
        request.setCreated(LocalDateTime.now());
        return requestMapper.requestToParticipationRequestDto(requestRepository.save(request));
    }

    //Отменить запрос может только тот кто его и создавал
    public ParticipationRequestDto cancelRequest(Integer userId, Integer requestId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        Request request = requestRepository.findById(requestId).orElseThrow(() -> new RequestNotFoundException(requestId));
        if (!request.getRequester().getId().equals(userId)) {
            throw new RequestNotFoundException(requestId);
        }
        request.setStatus(Status.CANCELED);
        requestRepository.save(request);
        return requestMapper.requestToParticipationRequestDto(request);
    }

}
