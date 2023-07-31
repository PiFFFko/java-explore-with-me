package ru.practicum.model.compilation.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.model.event.dto.EventShortDto;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationDto {
    List<EventShortDto> events;
    Integer id;
    Boolean pinned;
    String title;

}
