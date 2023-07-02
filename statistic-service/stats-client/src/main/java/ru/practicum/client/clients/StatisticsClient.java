package ru.practicum.client.clients;

import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticsClient {

    ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);

    void addHit(String app, String ip, String uri, LocalDateTime timestamp);
}