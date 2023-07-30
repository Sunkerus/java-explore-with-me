package ru.practicum.main.event.dto.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.main.event.dto.CommentDto;
import ru.practicum.main.event.dto.CommentFullDto;
import ru.practicum.main.event.dto.CommentIncomeDto;
import ru.practicum.main.event.enums.CommentState;
import ru.practicum.main.event.model.Comment;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.helper.DTFormatter;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public Comment toComment(CommentIncomeDto commentIncomeDto, User user, Event event) {
        return Comment.builder()
                .author(user)
                .text(commentIncomeDto.getText())
                .created(LocalDateTime.now())
                .event(event)
                .state(CommentState.PUBLISHED)
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorId(comment.getAuthor().getId())
                .created(comment.getCreated().format(DTFormatter.DATE_TIME_FORMATTER))
                .text(comment.getText())
                .eventId(comment.getEvent().getId())
                .state(comment.getState())
                .build();
    }

    public CommentFullDto toCommentFullDto(Comment comment, Integer confirmedRequests) {
        return CommentFullDto.builder()
                .id(comment.getId())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .created(comment.getCreated().format(DTFormatter.DATE_TIME_FORMATTER))
                .text(comment.getText())
                .event(EventMapper.toEventCommentDto(comment.getEvent(), confirmedRequests))
                .state(comment.getState())
                .build();
    }
}
