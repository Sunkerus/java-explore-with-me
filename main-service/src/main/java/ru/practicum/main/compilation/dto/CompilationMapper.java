package ru.practicum.main.compilation.dto;

import lombok.experimental.UtilityClass;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.model.Event;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> allByIdIn) {
        return Compilation.builder()
                .events(allByIdIn)
                .title(newCompilationDto.getTitle())
                .pinned(newCompilationDto.isPinned())
                .build();
    }

    public CompilationDto toDto(Compilation compilation, List<EventShortDto> eventsShort) {
        return CompilationDto.builder()
                .events(eventsShort)
                .id(compilation.getId())
                .pinned(compilation.isPinned())
                .title(compilation.getTitle())
                .build();
    }

    public CompilationDto toCompilationDtoList(Compilation compilation, List<EventShortDto> eventShortList) {
        return CompilationMapper.toDto(compilation, eventShortList);
    }

    public List<CompilationDto> toCompilationDtoList(List<Compilation> compilations, List<EventShortDto> eventShortList) {
        if (compilations.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, EventShortDto> eventsMap = eventShortList.stream()
                .collect(Collectors.toMap(EventShortDto::getId, Function.identity()));

        return compilations.stream()
                .map(compilation -> {
                    List<EventShortDto> eventShortDtoList = compilation.getEvents().stream()
                            .map(event -> eventsMap.get(event.getId()))
                            .collect(Collectors.toList());

                    return CompilationMapper.toDto(compilation, eventShortDtoList);
                })
                .collect(Collectors.toList());
    }

}
