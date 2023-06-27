package ru.practicum.stats.sevice;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.common.structures.HitDto;
import ru.practicum.common.structures.ViewStats;
import ru.practicum.stats.mapper.EndpointMapper;
import ru.practicum.stats.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique) {
        if (end.isBefore(start)) {
            throw new IllegalArgumentException(String
                    .format("Incorrect time interval, the start=%s cannot be later than end=%s", start, end));
        }

        if (uri == null || uri.isEmpty()) {
            if (unique) {
                return statsRepository.getAllStatsByDistinctIp(start, end);
            } else {
                return statsRepository.getAllStats(start, end);
            }
        } else {
            if (unique) {
                return statsRepository.getStatsByUrisDistinctIps(uri, start, end);
            } else {
                return statsRepository.getStatsByUris(uri, start, end);
            }
        }
    }

    @Override
    @Transactional
    public HitDto addHit(HitDto hitDto) {
        return EndpointMapper.mapToEndpointHitDto(statsRepository.save(EndpointMapper.mapToEndpointHit(hitDto)));
    }

}
