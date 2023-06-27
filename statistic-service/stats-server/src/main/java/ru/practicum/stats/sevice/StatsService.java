package ru.practicum.stats.sevice;

import ru.practicum.common.structures.HitDto;
import ru.practicum.common.structures.TransferStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    HitDto addHit(HitDto hitDto);

    List<TransferStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uri, boolean unique);
}
