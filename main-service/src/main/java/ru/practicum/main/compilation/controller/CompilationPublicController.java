package ru.practicum.main.compilation.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.service.CompilationService;
import ru.practicum.main.helper.FurtherPageRequest;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationPublicController {

    private final CompilationService compilationService;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        return compilationService.getCompilationByIdAsPublic(compId);
    }

    @GetMapping
    public List<CompilationDto> getAllCompilations(
            @RequestParam(defaultValue = "false")boolean pinned,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Max(1) int size) {
        return compilationService.getAllCompilationAsPublic(pinned, new FurtherPageRequest(from, size));
    }


}
