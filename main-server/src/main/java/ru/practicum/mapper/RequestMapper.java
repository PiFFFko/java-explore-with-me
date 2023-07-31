package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.model.request.Request;
import ru.practicum.model.request.dto.ParticipationRequestDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {

    @Mapping(target = "event", source = "request.event.id")
    @Mapping(target = "requester", source = "request.requester.id")
    ParticipationRequestDto requestToParticipationRequestDto(Request request);

    List<ParticipationRequestDto> listRequestsToListParticipationRequestDto(List<Request> requestList);
}
