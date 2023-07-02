package ru.practicum.main.event.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.structures.ViewStats;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.category.service.CategoryService;
import ru.practicum.main.event.dto.*;
import ru.practicum.main.event.dto.mappers.EventMapper;
import ru.practicum.main.event.dto.mappers.LocationMapper;
import ru.practicum.main.event.enums.EventSortType;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.enums.EventStateAction;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.event.model.QEvent;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.event.repository.LocationRepository;
import ru.practicum.main.exeption.BadRequest;
import ru.practicum.main.exeption.IssueException;
import ru.practicum.main.exeption.NotFoundException;
import ru.practicum.main.helper.FurtherPageRequest;
import ru.practicum.main.helper.QPredicates;
import ru.practicum.main.statistic.StatsService;
import ru.practicum.main.user.model.User;
import ru.practicum.main.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;

    private final CategoryService categoryService;

    private final LocationRepository locationRepository;

    private final StatsService statsService;

    private final UserService userService;


    private void validationEventDate(LocalDateTime newEventDate, LocalDateTime dateBeforeEventStart) {
        if (newEventDate != null && newEventDate.isBefore(dateBeforeEventStart)) {
            throw new BadRequest(String.format(
                    "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s", newEventDate));
        }
    }

    private Location saveAndGetLocation(LocationDto location) {
        return locationRepository.findByLatAndLon(location.getLat(), location.getLon())
                .orElseGet(() -> locationRepository.save(LocationMapper.toLocation(location)));
    }

    private void validationRangeTime(LocalDateTime rangeStart, LocalDateTime rangeEnd) {
        if (rangeStart != null && rangeEnd != null && (rangeStart.isAfter(rangeEnd))) {
            throw new BadRequest(
                    String.format("Field: rangeStart. Error: rangeStart cannot be after rangeEnd. Value: %s", rangeStart));
        }
    }


    @Override
    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id: %d cannot find", eventId)));
    }

    @Override
    public List<Event> getEventsByListOfIds(Set<Long> events) {
        if (events.isEmpty()) {
            return Collections.emptyList();
        }
        return eventRepository.findAllByIdIn(events);
    }

    @Override
    public List<EventShortDto> getAllEventsAsPublic(EventRequest req) {
        validationRangeTime(req.getRangeStart(), req.getRangeEnd());

        List<Event> events;
        Map<Long, Long> views;
        Predicate pred = QPredicates.build()
                .add(QEvent.event.state.eq(EventState.PUBLISHED))
                .add(!req.getText().isBlank() ? QEvent.event.annotation.containsIgnoreCase(req.getText())
                        .or(QEvent.event.description.containsIgnoreCase(req.getText())) : null)
                .add(req.isOnlyAvailable() ? QEvent.event.confirmedRequests.lt(QEvent.event.participantLimit)
                        .or(QEvent.event.participantLimit.eq(0)) : null)
                .add(req.getPaid() != null ? QEvent.event.paid.eq(req.getPaid()) : null)
                .add(req.getCategories() == null || req.getCategories().isEmpty() ? null : QEvent.event.category
                        .id.in(req.getCategories()))
                .add(req.getRangeStart() != null && req.getRangeEnd() != null
                        ? QEvent.event.eventDate.between(req.getRangeStart(), req.getRangeEnd()) : QEvent.event.eventDate
                        .after(LocalDateTime.now()))
                .buildAnd();

        if (EventSortType.EVENT_DATE.equals(req.getSort())) {
            FurtherPageRequest pageRequest = new FurtherPageRequest(req.getFrom(), req.getSize(), Sort.by("eventDate"));
            events = eventRepository.findAll(pred, pageRequest).getContent();
            views = getMapOfShowContext(events);
            statsService.addHit(req.getRequest());
            return events.stream().map(event ->
                            EventMapper.toShortDto(event, views.getOrDefault(event.getId(), 0L)))
                    .collect(Collectors.toList());
        }

        events = eventRepository.findAll(pred, new FurtherPageRequest(req.getFrom(), req.getSize())).getContent();
        views = getMapOfShowContext(events);
        statsService.addHit(req.getRequest());

        List<EventShortDto> eventShortDtos = events.stream()
                .map(event ->
                        EventMapper.toShortDto(event, views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());

        if (EventSortType.VIEWS.equals(req.getSort()) && !views.isEmpty()) {
            return eventShortDtos.stream().sorted(Comparator.comparing(EventShortDto::getViews).reversed()).collect(Collectors.toList());
        }

        return eventShortDtos;
    }

    @Override
    public List<EventShortDto> getEventsLikeShortDto(Set<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Event> events = eventRepository.findAllByIdIn(eventIds);
        Map<Long, Long> views = getMapOfShowContext(events);

        return events.stream()
                .map(event -> EventMapper.toShortDto(event, views.getOrDefault(event.getId(), 0L)
                )).collect(Collectors.toList());
    }

    @Override
    public List<EventShortDto> getAllEventsByUserId(Long userId, Pageable page) {
        userService.getUserById(userId);

        List<Event> events = eventRepository.findByInitiatorId(userId, page);
        Map<Long, Long> views = getMapOfShowContext(events);
        return events.stream().map(event ->
                        EventMapper.toShortDto(event, views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventFullDto> getEventsAsAdmin(EventRequest.AdminRequest req) {
        validationRangeTime(req.getRangeStart(), req.getRangeEnd());

        List<Event> events;
        Map<Long, Long> views;

        Predicate predicate = QPredicates.build()
                .add(req.getUsers() != null ? QEvent.event.initiator.id.in(req.getUsers()) : null)
                .add(req.getStates() != null ? QEvent.event.state.in(req.getStates()) : null)
                .add(req.getCategories() != null ? QEvent.event.category.id.in(req.getCategories()) : null)
                .add(req.getRangeStart() != null ? QEvent.event.eventDate.goe(req.getRangeStart()) : null)
                .add(req.getRangeEnd() != null ? QEvent.event.eventDate.loe(req.getRangeEnd()) : null)
                .buildAnd();

        if (predicate != null) {
            events = eventRepository.findAll(
                    predicate, new FurtherPageRequest(req.getFrom(), req.getSize())).getContent();
            views = getMapOfShowContext(events);
            return events.stream()
                    .map(event ->
                            EventMapper.toFullDto(event, views.getOrDefault(event.getId(), 0L)))
                    .collect(Collectors.toList());
        }

        events = eventRepository.findAll(new FurtherPageRequest(req.getFrom(), req.getSize())).getContent();
        views = getMapOfShowContext(events);

        return events.stream()
                .map(event ->
                        EventMapper.toFullDto(event, views.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    @Override
    public EventFullDto getEventByIdAsPrivate(Long userId, Long eventId) {
        userService.getUserById(userId);
        Event event = eventRepository.findByIdAndInitiatorId(eventId, userId)
                .orElseThrow(() -> new NotFoundException(
                        String.format("Event with id=%d was not found", eventId)));

        return EventMapper.toFullDto(event, getMapOfShowContext(List.of(event)).getOrDefault(event.getId(), 0L));
    }


    @Override
    @Transactional
    public EventFullDto patchingEventAsPrivate(UserEventRequestDto userEventRequestDto, Long userId, Long eventId) {
        validationEventDate(userEventRequestDto.getEventDate(), LocalDateTime.now().plusHours(2));

        Event event = getEventById(eventId);
        userService.getUserById(userId);

        if (EventState.PUBLISHED.equals(event.getState())) {
            throw new IssueException("Only pending or canceled events can be changed");
        }

        if (userEventRequestDto.getAnnotation() != null) {
            event.setAnnotation(userEventRequestDto.getAnnotation());
        }

        if (userEventRequestDto.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(userEventRequestDto.getCategory()));
        }

        if (userEventRequestDto.getDescription() != null) {
            event.setDescription(userEventRequestDto.getDescription());
        }

        if (userEventRequestDto.getEventDate() != null) {
            event.setEventDate(userEventRequestDto.getEventDate());
        }

        if (userEventRequestDto.getLocation() != null) {
            event.setLocation(saveAndGetLocation(userEventRequestDto.getLocation()));
        }

        if (userEventRequestDto.getParticipantLimit() != null) {
            event.setParticipantLimit(userEventRequestDto.getParticipantLimit());
        }

        if (userEventRequestDto.getPaid() != null) {
            event.setPaid(userEventRequestDto.getPaid());
        }

        if (userEventRequestDto.getRequestModeration() != null) {
            event.setRequestModeration(userEventRequestDto.getRequestModeration());
        }

        if (userEventRequestDto.getTitle() != null) {
            event.setTitle(userEventRequestDto.getTitle());
        }

        if (userEventRequestDto.getStateAction() != null) {
            if (EventStateAction.SEND_TO_REVIEW.equals(userEventRequestDto.getStateAction())) {
                event.setState(EventState.PENDING);
            } else if (EventStateAction.CANCEL_REVIEW.equals(userEventRequestDto.getStateAction())) {
                event.setState(EventState.CANCELED);
            }
        }

        return EventMapper.toEventFullDto(eventRepository.save(event), getMapOfShowContext(List.of(event)));
    }

    @Override
    @Transactional
    public EventFullDto patchEventAsAdmin(EventAdminRequestDto eventRequest, Long eventId) {
        validationEventDate(eventRequest.getEventDate(), LocalDateTime.now().plusHours(1));

        Event event = getEventById(eventId);

        if (eventRequest.getStateAction() != null && eventRequest.getStateAction().equals(EventStateAction.REJECT_EVENT)
                && event.getState().equals(EventState.PUBLISHED)) {
            throw new IssueException(
                    String.format("Cannot reject the event because it's not in the right state: %s", event.getState()));
        }

        if (eventRequest.getStateAction() != null && eventRequest.getStateAction().equals(EventStateAction.PUBLISH_EVENT)
                && !event.getState().equals(EventState.PENDING)) {
            throw new IssueException(
                    String.format("Cannot publish the event because it's not in the right state: %s", event.getState()));
        }

        if (eventRequest.getAnnotation() != null) {
            event.setAnnotation(eventRequest.getAnnotation());
        }

        if (eventRequest.getDescription() != null) {
            event.setDescription(eventRequest.getDescription());
        }

        if (eventRequest.getCategory() != null) {
            event.setCategory(categoryService.getCategoryById(eventRequest.getCategory()));
        }

        if (eventRequest.getEventDate() != null) {
            event.setEventDate(eventRequest.getEventDate());
        }

        if (eventRequest.getPaid() != null) {
            event.setPaid(eventRequest.getPaid());
        }

        if (eventRequest.getLocation() != null) {
            event.setLocation(saveAndGetLocation(eventRequest.getLocation()));
        }

        if (eventRequest.getParticipantLimit() != null) {
            event.setParticipantLimit(eventRequest.getParticipantLimit());
        }

        if (eventRequest.getRequestModeration() != null) {
            event.setRequestModeration(eventRequest.getRequestModeration());
        }

        if (eventRequest.getTitle() != null) {
            event.setTitle(eventRequest.getTitle());
        }

        if (eventRequest.getStateAction() != null) {
            switch (eventRequest.getStateAction()) {
                case PUBLISH_EVENT:
                    event.setState(EventState.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                case REJECT_EVENT:
                    event.setState(EventState.CANCELED);
                    break;
                default:
                    throw new IssueException(
                            String.format("Problem: incorrect state: %s", eventRequest.getStateAction()));
            }
        }

        return EventMapper.toEventFullDto(eventRepository.save(event), getMapOfShowContext(List.of(event)));
    }


    @Override
    @Transactional
    public EventFullDto creatingEventByUser(NewEventDto newEventDto, Long userId) {
        validationEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        User user = userService.getUserById(userId);
        Category category = categoryService.getCategoryById(newEventDto.getCategory());
        Location location = saveAndGetLocation(newEventDto.getLocation());
        Event event = eventRepository.save(EventMapper.toEvent(newEventDto, category, user, location));

        return EventMapper.toFullDto(eventRepository.save(event));
    }


    @Override
    public EventFullDto getEventAsPublicById(Long id, HttpServletRequest request) {
        Event event = eventRepository.findByIdAndState(id, EventState.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format("Event with id=%d was not found", id)));

        statsService.addHit(request);
        return EventMapper.toFullDto(event, getMapOfShowContext(List.of(event)).getOrDefault(event.getId(), 0L));
    }



    public Map<Long, Long> getMapOfShowContext(List<Event> events) {
        Map<Long, Long> statsByEventId = new HashMap<>();

        if (!events.iterator().hasNext()) {
            return statsByEventId;
        }

        List<Event> publishedEvents = events.stream()
                .filter(event -> event.getPublishedOn() != null)
                .collect(Collectors.toList());


        if (publishedEvents.isEmpty()) {
            return statsByEventId;
        }

        LocalDateTime publishedOn = publishedEvents
                .stream()
                .min(Comparator.comparing(Event::getPublishedOn))
                .get().getPublishedOn();

        List<String> uris = publishedEvents.stream()
                .map(Event::getId)
                .map(id -> "/events/" + id)
                .collect(Collectors.toList());

        List<ViewStats> stats = statsService.getStats(publishedOn, LocalDateTime.now(), uris, true);

        statsByEventId = stats.stream()
                .collect(Collectors.toMap(entity -> (
                        Long.parseLong(entity.getUri().split("/")[2])), ViewStats::getHits));
        return statsByEventId;
    }
}
