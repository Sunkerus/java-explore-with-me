package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.event.enums.CommentState;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
public class CommentDto {

    private Long id;

    private String text;

    private Long eventId;

    private Long authorId;

    private LocalDateTime created;

    private CommentState state;
}