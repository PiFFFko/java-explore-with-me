package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.exception.CompilationNotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.compilation.Compilation;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationDto;
import ru.practicum.model.event.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;
    private final EventRepository eventRepository;

    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<CompilationDto> result = new ArrayList<>();
        int page = from > 0 ? from / size : 0;
        Pageable compilationPageable = PageRequest.of(page, size);
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, compilationPageable);
        for (Compilation compilation : compilations) {
            CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
            compilationDto.setEvents(eventMapper.listEventsToListEventShortDto(compilation.getEvents()));
            result.add(compilationDto);
        }
        return result;
    }

    public CompilationDto getCompilation(Integer compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() -> new CompilationNotFoundException(compilationId));
        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        compilationDto.setEvents(eventMapper.listEventsToListEventShortDto(compilation.getEvents()));
        return compilationDto;
    }

    public CompilationDto createCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = compilationMapper.newCompilationDtoToCompilation(newCompilationDto);
        if (newCompilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());
            compilation.setEvents(events);
        }
        compilation = compilationRepository.save(compilation);
        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilation);
        compilationDto.setEvents(eventMapper.listEventsToListEventShortDto(compilation.getEvents()));
        return compilationDto;
    }

    public void deleteCompilation(Integer compilationId) {
        Compilation compilation = compilationRepository.findById(compilationId).orElseThrow(() -> new CompilationNotFoundException(compilationId));
        compilationRepository.deleteById(compilationId);
    }

    public CompilationDto updateCompilation(Integer compilationId, UpdateCompilationDto updateCompilationDto) {
        Compilation compilationToUpdate = compilationRepository.findById(compilationId).orElseThrow(() -> new CompilationNotFoundException(compilationId));
        if (updateCompilationDto.getEvents() != null) {
            List<Event> events = eventRepository.findAllById(updateCompilationDto.getEvents());
            compilationToUpdate.setEvents(events);
        }
        if (updateCompilationDto.getPinned() != null) {
            compilationToUpdate.setPinned(updateCompilationDto.getPinned());
        }
        if (updateCompilationDto.getTitle() != null) {
            compilationToUpdate.setTitle(updateCompilationDto.getTitle());
        }
        compilationToUpdate = compilationRepository.save(compilationToUpdate);
        CompilationDto compilationDto = compilationMapper.compilationToCompilationDto(compilationToUpdate);
        compilationDto.setEvents(eventMapper.listEventsToListEventShortDto(compilationToUpdate.getEvents()));
        return compilationDto;
    }


}
