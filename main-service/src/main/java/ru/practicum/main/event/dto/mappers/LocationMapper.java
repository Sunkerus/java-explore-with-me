package ru.practicum.main.event.dto.mappers;

import lombok.experimental.UtilityClass;
import ru.practicum.main.event.dto.LocationDto;
import ru.practicum.main.event.model.Location;

@UtilityClass
public class LocationMapper {

    public Location toLocation(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }

    public LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }
}
