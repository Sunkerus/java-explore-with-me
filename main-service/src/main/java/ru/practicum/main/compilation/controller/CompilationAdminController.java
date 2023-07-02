package ru.practicum.main.compilation.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.dto.CompilationRequest;
import ru.practicum.main.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
public class CompilationAdminController {

    private final CompilationService compilationService;

    @PatchMapping("/{compId}")
    @ResponseStatus(HttpStatus.OK)
    public CompilationDto patchCompilationViaAdmin(
            @Valid @RequestBody CompilationRequest compilationRequest,
            @PathVariable Long compId) {
        return compilationService.patchCompilationAsAdmin(compilationRequest, compId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto createCompilationViaAdmin(@Valid @RequestBody NewCompilationDto newCompilationDto) {
        return compilationService.createCompilationAsAdmin(newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilationByIdViaAdmin(@PathVariable Long compId) {
        compilationService.deleteCompilationAsAdmin(compId);
    }

}
