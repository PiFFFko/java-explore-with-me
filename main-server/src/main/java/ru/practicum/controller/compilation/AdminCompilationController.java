package ru.practicum.controller.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.model.compilation.dto.CompilationDto;
import ru.practicum.model.compilation.dto.NewCompilationDto;
import ru.practicum.model.compilation.dto.UpdateCompilationDto;
import ru.practicum.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/compilations")
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<CompilationDto> createCompilation(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(compilationService.createCompilation(newCompilationDto));
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Integer compilationId) {
        compilationService.deleteCompilation(compilationId);
    }

    @PatchMapping("/{compilationId}")
    public ResponseEntity<CompilationDto> updateCompilation(@PathVariable Integer compilationId,
                                                            @RequestBody @Valid UpdateCompilationDto updateCompilationDto) {
        return ResponseEntity.ok().body(compilationService.updateCompilation(compilationId, updateCompilationDto));
    }


}
