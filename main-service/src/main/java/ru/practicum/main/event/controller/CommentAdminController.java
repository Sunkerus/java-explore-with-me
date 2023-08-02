package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.CommentFullDto;
import ru.practicum.main.event.dto.CommentIncomingDto;
import ru.practicum.main.event.service.CommentService;
import ru.practicum.main.helper.DTFormatter;
import ru.practicum.main.helper.FurtherPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentService commentService;


    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto getCommentAsAdmin(@PathVariable Long commentId) {
        return commentService.getCommentAsAdmin(commentId);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentFullDto> getAllCommentsAsAdmin(
            @RequestParam(required = false) Long eventId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(defaultValue = "") String text,
            @RequestParam(required = false) @DateTimeFormat(pattern = DTFormatter.YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DTFormatter.YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        return commentService.getAllCommentsAsAdmin(
                eventId,
                authorId,
                text,
                rangeStart, rangeEnd,
                new FurtherPageRequest(from, size, Sort.by("created").descending()));
    }


    @PatchMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentFullDto patchCommentAsAdmin(
            @RequestBody @Valid CommentIncomingDto commentIncomingDto,
            @PathVariable Long commentId,
            @RequestParam Long eventId) {
        return commentService.patchCommentAsAdmin(commentIncomingDto, commentId, eventId);
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentAsAdmin(@PathVariable Long commentId) {
        commentService.deleteCommentAsAdmin(commentId);
    }


}
