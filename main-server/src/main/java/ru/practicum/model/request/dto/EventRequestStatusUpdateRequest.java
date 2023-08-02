package ru.practicum.model.request.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.enums.Status;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventRequestStatusUpdateRequest {
    List<Integer> requestIds;
    Status status;
}
