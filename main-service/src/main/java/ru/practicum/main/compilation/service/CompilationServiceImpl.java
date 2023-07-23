package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.structures.ViewStats;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationMapper;
import ru.practicum.main.compilation.dto.CompilationRequest;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.mappers.EventMapper;
import ru.practicum.main.event.enums.RequestStatus;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Request;
import ru.practicum.main.event.repository.EventRepository;
import ru.practicum.main.event.repository.RequestRepository;
import ru.practicum.main.exeption.NotFoundException;
import ru.practicum.main.statistic.StatsService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final StatsService statsService;

    @Override
    @Transactional
    public CompilationDto createCompilationAsAdmin(NewCompilationDto newCompilationDto) {
        List<Event> eventsByIds = Collections.emptyList();

        if (!newCompilationDto.getEvents().isEmpty()) {
            eventsByIds = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        }

        Set<Long> eventsId = newCompilationDto.getEvents();

        List<EventShortDto> eventShortDtoList;

        if (eventsId != null) {
            eventShortDtoList = getEventsLikeShortDto(eventsId);
        } else {
            eventShortDtoList = new ArrayList<>();
        }

        return CompilationMapper.toCompilationDtoList(compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, eventsByIds)),
                eventShortDtoList);
    }

    @Override
    @Transactional
    public CompilationDto patchCompilationAsAdmin(CompilationRequest compilationRequest, Long compId) {
        Compilation compilation = getCompilationById(compId);

        if (compilationRequest.getPinned() != null) {
            compilation.setPinned(compilationRequest.getPinned());
        }

        if (compilationRequest.getTitle() != null) {
            compilation.setTitle(compilationRequest.getTitle());
        }

        if (compilationRequest.getEvents() != null) {
            List<Event> events = Collections.emptyList();
            if (!compilationRequest.getEvents().isEmpty()) {
                events = eventRepository.findAllByIdIn(compilationRequest.getEvents());
            }
            compilation.setEvents(events);
        }

        Set<Long> eventsId = compilationRequest.getEvents();

        List<EventShortDto> eventShortDtoList;

        if (eventsId != null) {
            eventShortDtoList = getEventsLikeShortDto(eventsId);
        } else {
            eventShortDtoList = new ArrayList<>();
        }

        return CompilationMapper.toCompilationDtoList(compilationRepository.save(compilation),
                eventShortDtoList);
    }

    @Override
    @Transactional
    public void deleteCompilationAsAdmin(Long compId) {
        getCompilationById(compId);
        compilationRepository.deleteById(compId);
    }


    @Override
    public CompilationDto getCompilationByIdAsPublic(Long id) {
        Compilation compilation = getCompilationById(id);

        Set<Long> eventsId = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toSet());

        return CompilationMapper.toCompilationDtoList(
                compilation,
                getEventsLikeShortDto(eventsId));
    }

    @Override
    public List<CompilationDto> getAllCompilationAsPublic(boolean pinned, Pageable page) {

        List<Compilation> compilationList;

        Set<Long> eventIds;

        if (pinned) {
            compilationList = compilationRepository.findAllByPinned(true, page);
            eventIds = compilationList
                    .stream().flatMap(compilation -> compilation.getEvents()
                            .stream()
                            .map(Event::getId))
                    .collect(Collectors.toSet());

        } else {
            compilationList = compilationRepository.findAll(page).getContent();
            eventIds = compilationList
                    .stream().flatMap(compilation -> compilation.getEvents()
                            .stream()
                            .map(Event::getId))
                    .collect(Collectors.toSet());

        }
        return CompilationMapper.toCompilationDtoList(compilationList,
                getEventsLikeShortDto(eventIds));


    }

    private Compilation getCompilationById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Compilation with id: %d cannot found", compId)));
    }


    private List<EventShortDto> getEventsLikeShortDto(Set<Long> eventIds) {
        if (eventIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<Event> events = eventRepository.findAllByIdIn(eventIds);
        Map<Long, Long> views = getMapOfShowContext(events);


        List<Request> requests = requestRepository.findByStatusAndEvent_IdIn(RequestStatus.CONFIRMED,
                eventIds);

        Map<Long, Long> confirmedRequests = new HashMap<>();

        for (Request request : requests) {
            long value;
            if (confirmedRequests.containsKey(request.getEvent().getId())) {
                value = confirmedRequests.get(request.getEvent().getId()) + 1L;
            } else {
                value = 1L;
            }
            confirmedRequests.put(request.getEvent().getId(), value);
        }

        return events.stream()
                .map(event -> EventMapper.toShortDto(event, views.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L).intValue())).collect(Collectors.toList());
    }

    private Map<Long, Long> getMapOfShowContext(List<Event> events) {
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
