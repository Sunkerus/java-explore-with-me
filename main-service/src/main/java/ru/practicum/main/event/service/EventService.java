package ru.practicum.main.event.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.model.Event;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface EventService {

    Event getEventById(Long eventId);

    List<Event> getEventsByListOfIds(Set<Long> events);

    Collection<EventShortDto> getAllEventsAsPublic(EventRequest req);

    List<EventShortDto> getEventsLikeShortDto(Set<Long> events);

    List<EventShortDto> getAllEventsByUserId(Long userId, Pageable page);

    List<EventFullDto> getEventsAsAdmin(EventRequest.AdminRequest adminReq);

    EventFullDto getEventByIdAsPrivate(Long userId, Long eventId);

    EventFullDto patchingEventAsPrivate(UserEventRequestDto userEventRequestDto, Long userId, Long eventId);

    EventFullDto patchEventAsAdmin(EventAdminRequestDto eventAdminRequestDto, Long eventId);

    EventFullDto creatingEventByUser(NewEventDto newEventDto, Long userId);

    EventFullDto getEventAsPublicById(Long id, HttpServletRequest requestURI);
}


