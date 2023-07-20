package ru.practicum.main.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.event.dto.*;

import java.util.Collection;
import java.util.List;

public interface EventService {

    Collection<EventShortDto> getAllEventsAsPublic(String ip, String uri, Integer from, Integer size, EventRequest req);

    List<EventShortDto> getAllEventsByUserId(Long userId, Pageable page);

    List<EventFullDto> getEventsAsAdmin(EventRequest.AdminRequest adminReq);

    EventFullDto getEventByIdAsPrivate(Long userId, Long eventId);

    EventFullDto patchingEventAsPrivate(UserEventRequestDto userEventRequestDto, Long userId, Long eventId);

    EventFullDto patchEventAsAdmin(EventAdminRequestDto eventAdminRequestDto, Long eventId);

    EventFullDto creatingEventByUser(NewEventDto newEventDto, Long userId);

    EventFullDto getEventAsPublicById(Long id, String ip, String uri);
}


