package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Getter;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserShortDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Getter
public class EventShortDto {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    @NotNull
    private Boolean paid;

    @NotBlank(message = "title cannot be blank")
    private String title;

    private Long views;

}
