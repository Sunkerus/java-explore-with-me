package ru.practicum.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import ru.practicum.common.StatsServiceThings;
import ru.practicum.common.structures.HitDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
public class StatsClient extends BaseClient {


    @Autowired
    public StatsClient(@Value("${stats-server.url}") String serverUrl, WebClient.Builder webClientBuilder) {
        super(webClientBuilder.baseUrl(serverUrl).build());
    }

    public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {

        Function<UriBuilder, URI> uriFunction = uriBuilder -> {
            UriBuilder builder = uriBuilder.path("/stats")
                    .queryParam("start", start.format(StatsServiceThings.DATE_TIME_FORMATTER))
                    .queryParam("end", end.format(StatsServiceThings.DATE_TIME_FORMATTER))
                    .queryParam("unique", unique);

            if (uris != null && !uris.isEmpty()) {
                String urisString = String.join(",", uris);
                builder.queryParam("uris", urisString);
            }
            return builder.build();
        };

        return get(uriFunction);
    }


    public ResponseEntity<Object> addHit(String app, String ip, String uri, LocalDateTime timestamp) {
        HitDto hitDto = HitDto.builder()
                .app(app)
                .ip(ip)
                .uri(uri)
                .timestamp(timestamp.format(StatsServiceThings.DATE_TIME_FORMATTER))
                .build();

        Function<UriBuilder, URI> uriFunction = uriBuilder -> {
            UriBuilder builder = uriBuilder.path("/hit");
            return builder.build();
        };

        return post(hitDto, uriFunction);
    }

}
