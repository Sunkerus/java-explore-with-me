package ru.practicum.main.compilation.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationRequest;
import ru.practicum.main.compilation.dto.NewCompilationDto;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilationAsAdmin(NewCompilationDto newCompilationDto);

    CompilationDto patchCompilationAsAdmin(CompilationRequest compilationRequest, Long compId);

    void deleteCompilationAsAdmin(Long compId);

    CompilationDto getCompilationByIdAsPublic(Long id);

    List<CompilationDto> getAllCompilationAsPublic(boolean pinned, Pageable pageRequest);
}
