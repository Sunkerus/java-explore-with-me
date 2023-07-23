package ru.practicum.main.compilation.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.main.event.dto.EventShortDto;

import java.util.List;


@Getter
@Builder
public class CompilationDto {

    private Long id;

    private Boolean pinned;

    private String title;

    private List<EventShortDto> events;
}
