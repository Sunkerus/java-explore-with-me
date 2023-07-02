package ru.practicum.client.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import ru.practicum.client.exceptions.StatisticException;
import ru.practicum.common.StatsServiceThings;
import ru.practicum.common.structures.HitDto;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

@Service
public class StatisticsClientImpl implements StatisticsClient {

    private final WebClient webClient;


    protected StatisticsClientImpl(@Value("${stats-server.url}") String serverUrl) {
        this.webClient = WebClient.builder()
                .defaultHeaders(httpHeaders -> {
                    httpHeaders.setContentType(MediaType.APPLICATION_JSON);
                    httpHeaders.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .baseUrl(serverUrl)
                .filter(handleError())
                .build();
    }

    private ExchangeFilterFunction handleError() {
        return ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
            if (clientResponse.statusCode().isError()) {
                HttpHeaders httpHeaders = clientResponse.headers().asHttpHeaders();
                if (httpHeaders.containsKey("X-Error-Class")) {
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(body -> Mono.error(new StatisticException(body.replace("/", ""))));
                }
            }
            return Mono.just(clientResponse);
        });
    }

    @Override
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

        return executeRequest(HttpMethod.GET, uriFunction, null, Object.class);
    }

    @Override
    public void addHit(String app, String ip, String uri, LocalDateTime timestamp) {
        HitDto endpointHitDto = HitDto.builder()
                .app(app)
                .ip(ip)
                .uri(uri)
                .timestamp(timestamp.format(StatsServiceThings.DATE_TIME_FORMATTER))
                .build();

        Function<UriBuilder, URI> uriFunction = uriBuilder -> {
            UriBuilder builder = uriBuilder.path("/hit");
            return builder.build();
        };

        executeRequest(HttpMethod.POST, uriFunction, endpointHitDto, Object.class);
    }

    private <T> ResponseEntity<Object> executeRequest(HttpMethod method, Function<UriBuilder, URI> uriFunction, T requestBody, Class<Object> responseType) {
        return webClient.method(method)
                .uri(uriFunction)
                .body(requestBody != null ? BodyInserters.fromValue(requestBody) : BodyInserters.empty())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.toEntity(responseType);
                    } else {
                        return Mono.just(ResponseEntity.status(response.rawStatusCode()).body(response.toEntity(String.class)));
                    }
                }).block();
    }
}