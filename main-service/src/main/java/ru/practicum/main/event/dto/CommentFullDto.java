package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.main.event.enums.CommentState;
import ru.practicum.main.user.dto.UserShortDto;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@Setter
@Builder
public class CommentFullDto {

    private Long id;

    private String text;

    private EventCommentDto event;

    private UserShortDto author;

    private String created;

    @Enumerated(EnumType.STRING)
    private CommentState state;

}
