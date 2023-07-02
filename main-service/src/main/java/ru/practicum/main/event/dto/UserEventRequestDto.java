package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.main.event.enums.EventStateAction;
import ru.practicum.main.helper.DTFormatter;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserEventRequestDto {

    @Size(min = 20, max = 2000, message = "Annotation size must be between 20 and 2000")
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000,message = "Description size must be between 20 and 7000")
    private String description;

    @JsonFormat(pattern = DTFormatter.YYYY_MM_DD_HH_MM_SS, shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    private LocationDto location;

    private Boolean paid;

    @PositiveOrZero(message = "participantLimit must be positive or zero")
    private Integer participantLimit;

    private Boolean requestModeration;

    private EventStateAction stateAction;

    @Size(min = 3, max = 120, message = "title size muse be between 3 and 120")
    private String title;
}
