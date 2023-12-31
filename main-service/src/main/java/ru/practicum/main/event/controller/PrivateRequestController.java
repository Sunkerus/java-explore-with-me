package ru.practicum.main.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.main.event.dto.ParticipationRequestDto;
import ru.practicum.main.event.service.RequestService;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {

    private final RequestService requestService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> getEventRequestsByRequester(@PathVariable Long userId) {
        return requestService.getEventRequestsByRequester(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto createEventRequestByRequester(@PathVariable Long userId, @RequestParam Long eventId) {
        return requestService.createEventRequestByRequester(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelEventRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        return requestService.cancelEventRequestByRequester(userId, requestId);
    }
}
