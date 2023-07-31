package ru.practicum.controller.event;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.enums.State;
import ru.practicum.model.event.dto.EventFullDto;
import ru.practicum.model.event.dto.UpdateEventDto;
import ru.practicum.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
public class AdminEventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventFullDto>> getEventsByAdmin(@RequestParam(required = false) List<Integer> users,
                                                              @RequestParam(required = false) List<State> states,
                                                              @RequestParam(required = false) List<Integer> categories,
                                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                              @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                              @RequestParam(defaultValue = "0") Integer from,
                                                              @RequestParam(defaultValue = "10") Integer size) {
        return ResponseEntity.ok().body(eventService.getEventsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventByAdmin(@PathVariable Integer eventId,
                                           @RequestBody @Valid UpdateEventDto updateEventAdminDto) {
        return ResponseEntity.ok().body(eventService.updateEventByAdmin(eventId, updateEventAdminDto));
    }


}
