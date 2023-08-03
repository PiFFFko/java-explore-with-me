package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.comment.dto.CommentDto;
import ru.practicum.model.comment.dto.NewCommentDto;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.EventShortDto;
import ru.practicum.model.event.dto.NewEventDto;
import ru.practicum.model.event.dto.UpdateEventDto;
import ru.practicum.model.request.dto.EventRequestStatusUpdateDto;
import ru.practicum.model.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.model.request.dto.ParticipationRequestDto;
import ru.practicum.service.EventService;

import javax.persistence.criteria.CriteriaBuilder;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/events")
public class PrivateEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getUserEvents(@PathVariable Integer userId,
                                                             @RequestParam(defaultValue = "0") Integer from,
                                                             @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(eventService.getUserEvents(userId, from, size));
    }

    @PostMapping
    public ResponseEntity<EventFullDto> createNewEvent(@PathVariable Integer userId,
                                                       @RequestBody @Valid NewEventDto newEventDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createNewEvent(userId, newEventDto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getUserEvent(@PathVariable Integer userId, @PathVariable Integer eventId) {
        return ResponseEntity.ok().body(eventService.getUserEvent(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable Integer userId,
                                                    @PathVariable Integer eventId,
                                                    @RequestBody @Valid UpdateEventDto updateEventDto) {
        return ResponseEntity.ok().body(eventService.updateEvent(userId, eventId, updateEventDto));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getEventRequestsForUser(@PathVariable Integer userId,
                                                                                 @PathVariable Integer eventId) {
        return ResponseEntity.ok().body(eventService.getEventRequestsForUser(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateDto> updateRequestStatus(@PathVariable Integer userId,
                                                           @PathVariable Integer eventId,
                                                           @RequestBody EventRequestStatusUpdateRequest statusUpdateRequest) {
        return ResponseEntity.ok().body(eventService.updateRequestStatus(userId, eventId, statusUpdateRequest));
    }

    @PostMapping("/{eventId}/comments")
    public ResponseEntity<CommentDto> createComment(@PathVariable Integer userId,
                                                    @PathVariable Integer eventId,
                                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(eventService.createComment(userId, eventId, newCommentDto));
    }

    @PatchMapping("/{eventId}/comments/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Integer userId,
                                                    @PathVariable Integer eventId,
                                                    @PathVariable Integer commentId,
                                                    @RequestBody @Valid NewCommentDto newCommentDto) {
        return ResponseEntity.ok().body(eventService.updateComment(userId, eventId, commentId, newCommentDto));
    }

    @DeleteMapping("/{eventId}/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@PathVariable Integer userId,
                              @PathVariable Integer eventId,
                              @PathVariable Integer commentId) {
        eventService.deleteComment(userId, eventId, commentId);
    }


}




