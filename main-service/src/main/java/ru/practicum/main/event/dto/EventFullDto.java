package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.category.dto.CategoryDto;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.user.dto.UserShortDto;


@Builder
@Getter
@Setter
public class EventFullDto {

    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    private String createdOn;

    private String description;

    private String eventDate;

    private Long id;

    private UserShortDto initiator;

    private LocationDto location;

    private Boolean paid;

    private int participantLimit;

    private String publishedOn;

    private boolean requestModeration;

    private EventState state;

    private String title;

    private Long views;
}
