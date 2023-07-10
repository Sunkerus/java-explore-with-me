package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.event.service.RequestService;
import ru.practicum.main.helper.FurtherPageRequest;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class PrivateEventController {

    private final EventService eventService;

    private final RequestService requestService;

    @GetMapping("/{eventId}/requests")
    public List<ParticipationRequestDto> getEventRequestsAsPublic(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.getEventRequestsByEventOwner(userId, eventId);
    }

    @GetMapping("/{eventId}")
    public EventFullDto getEventAsPrivate(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEventByIdAsPrivate(userId, eventId);
    }

    @GetMapping
    public List<EventShortDto> getEventAsPublic(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") @Min(0) int from,
            @RequestParam(defaultValue = "10") int size) {
        return eventService.getAllEventsByUserId(userId, new FurtherPageRequest(from, size));
    }


    @PatchMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto patchEventAsPrivate(
            @RequestBody @Valid UserEventRequestDto userEventRequestDto,
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return eventService.patchingEventAsPrivate(userEventRequestDto, userId, eventId);
    }

    @PatchMapping("/{eventId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public EventRequestStatusResult patchEventRequestAsPublic(
            @RequestBody EventRequestStatus eventRequestStatus,
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        return requestService.patchEventRequestByEventOwner(eventRequestStatus, userId, eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto createEventAsPrivate(@Valid @RequestBody NewEventDto newEventDto, @PathVariable Long userId) {
        return eventService.creatingEventByUser(newEventDto, userId);
    }
}
