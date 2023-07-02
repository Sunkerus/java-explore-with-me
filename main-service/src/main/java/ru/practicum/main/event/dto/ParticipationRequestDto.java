package ru.practicum.main.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ParticipationRequestDto {

    private String created;

    private Long event;

    private Long id;

    private Long requester;

    private String status;
}
