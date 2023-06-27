package ru.practicum.stats.sevice;

import ru.practicum.common.structures.HitDto;
import ru.practicum.common.structures.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    HitDto addHit(HitDto hitDto);

    List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique);
}
