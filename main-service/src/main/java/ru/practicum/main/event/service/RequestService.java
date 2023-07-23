package ru.practicum.main.event.service;

import ru.practicum.main.event.dto.EventRequestStatus;
import ru.practicum.main.event.dto.EventRequestStatusResult;
import ru.practicum.main.event.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto createEventRequestByRequester(Long userId, Long eventId);

    List<ParticipationRequestDto> getEventRequestsByRequester(Long userId);

    ParticipationRequestDto cancelEventRequestByRequester(Long userId, Long requestId);

    List<ParticipationRequestDto> getEventRequestsByEventOwner(Long userId, Long eventId);

    EventRequestStatusResult patchEventRequestByEventOwner(
            EventRequestStatus eventRequestStatus,
            Long userId,
            Long eventId);
}
