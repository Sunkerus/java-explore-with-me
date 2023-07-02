package ru.practicum.main.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.compilation.dto.CompilationDto;
import ru.practicum.main.compilation.dto.CompilationMapper;
import ru.practicum.main.compilation.dto.CompilationRequest;
import ru.practicum.main.compilation.dto.NewCompilationDto;
import ru.practicum.main.compilation.model.Compilation;
import ru.practicum.main.compilation.repository.CompilationRepository;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.service.EventService;
import ru.practicum.main.exeption.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;

    private final EventService eventService;

    @Override
    @Transactional
    public CompilationDto createCompilationAsAdmin(NewCompilationDto newCompilationDto) {
        List<Event> eventsByIds = new ArrayList<>();

        if (!newCompilationDto.getEvents().isEmpty()) {
            eventsByIds = eventService.getEventsByListOfIds(newCompilationDto.getEvents());
        }

        return CompilationMapper.toCompilationDtoList(newCompilationDto.getEvents(),
                compilationRepository.save(CompilationMapper.toCompilation(newCompilationDto, eventsByIds)),
                eventService);
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
            List<Event> events = eventService.getEventsByListOfIds(compilationRequest.getEvents());
            compilation.setEvents(events);
        }


        return CompilationMapper.toCompilationDtoList(compilationRequest.getEvents(),
                compilationRepository.save(compilation),
                eventService);
    }

    @Override
    @Transactional
    public void deleteCompilationAsAdmin(Long compId) {
        getCompilationById(compId);
        compilationRepository.deleteById(compId);
    }


    @Override
    public CompilationDto getCompilationByIdAsPublic(Long Id) {
        Compilation compilation = getCompilationById(Id);
        return CompilationMapper.toCompilationDtoList(
                compilation.getEvents().stream().map(Event::getId).collect(Collectors.toSet()),
                compilation,
                eventService);
    }

    public Compilation getCompilationById(Long compId) {
        return compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Compilation with id: %d cannot found", compId)));
    }


    @Override
    public List<CompilationDto> getAllCompilationAsPublic(boolean pinned, Pageable page) {
        if (pinned) {
            return CompilationMapper.toCompilationDtoList(compilationRepository.findAllByPinned(true, page),
                    eventService);
        } else {
            return CompilationMapper.toCompilationDtoList(compilationRepository.findAll(page).getContent(),
                    eventService);
        }


    }


}
