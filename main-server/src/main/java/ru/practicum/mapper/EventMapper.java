package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.event.Event;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.NewEventDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    List<EventShortDto> listEventsToListEventShortDto(List<Event> eventList);

    @Mapping(target = "category", ignore = true)
    Event newEventToEvent(NewEventDto eventDto);

    EventFullDto eventToEventFullDto(Event event);

    List<EventFullDto> listEventToListEventFullDto(List<Event> events);
}
