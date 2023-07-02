package ru.practicum.main.event.dto.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.main.category.dto.CategoryMapper;
import ru.practicum.main.category.model.Category;
import ru.practicum.main.event.dto.EventFullDto;
import ru.practicum.main.event.dto.EventShortDto;
import ru.practicum.main.event.dto.NewEventDto;
import ru.practicum.main.event.enums.EventState;
import ru.practicum.main.event.model.Event;
import ru.practicum.main.event.model.Location;
import ru.practicum.main.helper.DTFormatter;
import ru.practicum.main.user.mapper.UserMapper;
import ru.practicum.main.user.model.User;

import java.time.LocalDateTime;
import java.util.Map;

@UtilityClass
public class EventMapper {

    public Event toEvent(NewEventDto newEventDto, Category category, User user, Location location) {
        return Event.builder()
                .eventDate(newEventDto.getEventDate())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .location(location)
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .description(newEventDto.getDescription())
                .paid(newEventDto.isPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.isRequestModeration())
                .title(newEventDto.getTitle())
                .initiator(user)
                .confirmedRequests(0)
                .build();
    }

    public static EventShortDto toShortDto(Event event, Long views) {
        return EventShortDto.builder()
                .id(event.getId())
                .eventDate(event.getEventDate().format(DTFormatter.DATE_TIME_FORMATTER))
                .title(event.getTitle())
                .paid(event.isPaid())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .views(views)
                .initiator(UserMapper.tUserShortDto(event.getInitiator()))
                .build();
    }

    public EventFullDto toFullDto(Event event) {
        return halfBuild(event).views(0L).build();
    }

    public EventFullDto toFullDto(Event event, Long views) {
        return halfBuild(event).views(views).build();

    }

    public EventFullDto toEventFullDto(Event event, Map<Long, Long> views) {
        return EventMapper.toFullDto(event, views.getOrDefault(event.getId(), 0L));
    }

    private EventFullDto.EventFullDtoBuilder halfBuild(Event event) {
        return EventFullDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn().format(DTFormatter.DATE_TIME_FORMATTER))
                .description(event.getDescription())
                .eventDate(event.getEventDate().format(DTFormatter.DATE_TIME_FORMATTER))
                .id(event.getId())
                .initiator(UserMapper.tUserShortDto(event.getInitiator()))
                .location(LocationMapper.toDto(event.getLocation()))
                .paid(event.isPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn()
                        .format(DTFormatter.DATE_TIME_FORMATTER) : null)
                .requestModeration(event.isRequestModeration())
                .state(event.getState())
                .title(event.getTitle());
    }
}
