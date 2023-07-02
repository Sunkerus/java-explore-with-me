package ru.practicum.main.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank
    @Size(min = 20, max = 2000, message = "Annotation size must be between 20 and 2000")
    private String annotation;

    private Long category;

    @NotBlank
    @Size(min = 20, max = 7000, message = "Description size must be between 20 and 7000")
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", shape = JsonFormat.Shape.STRING)
    private LocalDateTime eventDate;

    private LocationDto location;

    private boolean paid;

    @PositiveOrZero(message = "ParticipantLimit must be positive or zero")
    private int participantLimit = 0;

    private boolean requestModeration = true;

    @Size(min = 3, max = 120, message = "title size must be between 3 and 120")
    private String title;
}
