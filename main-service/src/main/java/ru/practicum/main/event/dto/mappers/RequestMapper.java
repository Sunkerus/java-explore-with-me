package ru.practicum.main.event.dto.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.main.event.dto.ParticipationRequestDto;
import ru.practicum.main.event.model.Request;
import ru.practicum.main.helper.DTFormatter;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class RequestMapper {

    public ParticipationRequestDto toParticipationRequestDtos(Request request) {
        return ParticipationRequestDto.builder()
                .requester(request.getRequester().getId())
                .created(request.getCreated().format(DTFormatter.DATE_TIME_FORMATTER))
                .id(request.getId())
                .event(request.getEvent().getId())
                .status(request.getStatus().name())
                .build();
    }

    public List<ParticipationRequestDto> toParticipationRequestDtos(Iterable<Request> requests) {
        List<ParticipationRequestDto> dtos = new ArrayList<>();

        for (Request request : requests) {
            dtos.add(toParticipationRequestDtos(request));
        }

        return dtos;
    }
}
