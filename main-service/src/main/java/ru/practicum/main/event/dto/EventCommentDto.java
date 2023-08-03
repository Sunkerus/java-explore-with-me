package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.user.dto.UserShortDto;

@Builder
@Getter
@Setter
public class EventCommentDto {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;
}
