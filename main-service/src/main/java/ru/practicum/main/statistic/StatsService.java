package ru.practicum.main.statistic;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.common.structures.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final StatsClient statisticsClient;
    private final ObjectMapper mapper;
    @Value("${app.name}")
    private String appName;

    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        ResponseEntity<Object> stats = statisticsClient.getStats(start, end, uris, unique);
        return mapper.convertValue(stats.getBody(), new TypeReference<>() {
        });
    }

    public void addHit(String remoteAddr, String requestURI) {
        statisticsClient.addHit(appName, remoteAddr, requestURI, LocalDateTime.now());
    }
}
