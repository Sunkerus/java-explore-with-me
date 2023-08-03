package ru.practicum.main.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.event.dto.CommentDto;
import ru.practicum.main.event.dto.CommentFullDto;
import ru.practicum.main.event.dto.CommentIncomingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface CommentService {

    CommentDto postCommentAsPrivate(CommentIncomingDto newCommentDto, Long userId, Long eventId);

    CommentDto patchCommentAsPrivate(CommentIncomingDto newCommentDto, Long userId, Long eventId, Long commentId);

    void deleteCommentAsPrivate(Long userId, Long eventId, Long commentId);

    CommentDto getCommentAsPrivate(Long authorId, Long commentId);

    List<CommentDto> getAllOwnerCommentsAsPrivate(
            Long authorId,
            Long eventId,
            String text,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Pageable page);

    List<CommentDto> getAllCommentsAsPublic(
            Long eventId,
            String text,
            String authorName,
            LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Pageable page);

    CommentDto getCommentByIdAsPublic(Long commentId);

    CommentFullDto patchCommentAsAdmin(CommentIncomingDto commentIncomingDto, Long commentId, Long eventId);

    CommentFullDto getCommentAsAdmin(Long commentId);

    List<CommentFullDto> getAllCommentsAsAdmin(
            Long eventId,
            Long authorId,
            String text,
            LocalDateTime rangeStart, LocalDateTime rangeEnd,
            Pageable page);

    void deleteCommentAsAdmin(Long commentId);

}
