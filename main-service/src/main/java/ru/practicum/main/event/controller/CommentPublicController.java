package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.CommentDto;
import ru.practicum.main.event.service.CommentService;
import ru.practicum.main.helper.DTFormatter;
import ru.practicum.main.helper.FurtherPageRequest;

import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentPublicController {

    private final CommentService commentService;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getAllCommentsViaPublic(
            @RequestParam(required = false) Long eventId,
            @RequestParam(defaultValue = "") String text,
            @RequestParam(defaultValue = "") String authorName,
            @RequestParam(required = false) @DateTimeFormat(pattern = DTFormatter.YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DTFormatter.YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") @Min(1) int size) {
        return commentService.getAllCommentsViaPublic(
                eventId,
                text,
                authorName,
                rangeStart, rangeEnd,
                new FurtherPageRequest(from, size, Sort.by("created").descending()));
    }

    @GetMapping("/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto getCommentByIdViaPublic(@PathVariable Long commentId) {
        return commentService.getCommentByIdViaPublic(commentId);
    }
}