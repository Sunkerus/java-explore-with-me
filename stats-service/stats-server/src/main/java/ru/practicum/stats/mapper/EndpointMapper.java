package ru.practicum.stats.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.common.StatsServiceThings;
import ru.practicum.common.structures.HitDto;
import ru.practicum.stats.model.EndpointHit;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EndpointMapper {

    public static HitDto mapToEndpointHitDto(EndpointHit endpointHit) {
        return HitDto.builder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(endpointHit.getTimestamp().format(StatsServiceThings.DATE_TIME_FORMATTER))
                .build();
    }

    public static EndpointHit mapToEndpointHit(HitDto hitDto) {
        return EndpointHit.builder()
                .app(hitDto.getApp())
                .ip(hitDto.getIp())
                .uri(hitDto.getUri())
                .timestamp(LocalDateTime.parse(hitDto.getTimestamp(), StatsServiceThings.DATE_TIME_FORMATTER))
                .build();
    }


}
