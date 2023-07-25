package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventRequest;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.enums.EventSortType;
import ru.practicum.main.event.service.CommentService;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.helper.DTFormatter;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class PublicEventController {

    private final EventService eventService;

    private final CommentService commentService;

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdViaPublic(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEventAsPublicById(id, request.getRemoteAddr(), request.getRequestURI());
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<EventShortDto> getAllEventsByPublic(
            @RequestParam(defaultValue = "") String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) @DateTimeFormat(pattern = DTFormatter.YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = DTFormatter.YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeEnd,
            @RequestParam(defaultValue = "false") boolean onlyAvailable,
            @RequestParam(required = false) EventSortType sort,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        return eventService.getAllEventsAsPublic(request.getRemoteAddr(), request.getRequestURI(), from, size,
                EventRequest.of(
                        text,
                        categories,
                        paid,
                        rangeStart,
                        rangeEnd,
                        onlyAvailable,
                        sort));
    }


}
