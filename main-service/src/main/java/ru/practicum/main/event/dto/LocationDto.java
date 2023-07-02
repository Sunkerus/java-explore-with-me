package ru.practicum.main.event.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class LocationDto {

    private Double lat;

    private Double lon;
}
