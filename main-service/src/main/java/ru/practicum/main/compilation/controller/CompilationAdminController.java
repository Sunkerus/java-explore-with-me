package ru.practicum.main.compilation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationRequest;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patchCompilationAsAdmin(
            @Valid @RequestBody CompilationRequest compilationRequest,
            @PathVariable Long compId) {
        return compilationService.patchCompilationAsAdmin(compilationRequest, compId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilationAsAdmin(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.createCompilationAsAdmin(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationByIdAsAdmin(@PathVariable Long compId) {
        compilationService.deleteCompilationAsAdmin(compId);
    }

}
