package ru.practicum.client;

import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import ru.practicum.common.structures.HitDto;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

public abstract class BaseClient {

    private final WebClient webClient;


    protected BaseClient(final WebClient webClient) {
        this.webClient = webClient;
    }

    private <T> Mono<ResponseEntity<Object>> executeRequest(HttpMethod method, Function<UriBuilder, URI> uriFunction, T requestBody, Class<Object> responseType) {
        return webClient.method(method)
                .uri(uriFunction)
                .headers(headers -> {
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.setAccept(List.of(MediaType.APPLICATION_JSON));
                })
                .body(requestBody != null ? BodyInserters.fromValue(requestBody) : BodyInserters.empty())
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.toEntity(responseType);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }

    protected ResponseEntity<Object> get(Function<UriBuilder, URI> uriFunction) {
        return executeRequest(HttpMethod.GET, uriFunction, null, Object.class).block();
    }

    protected ResponseEntity<Object> post(HitDto hitDto, Function<UriBuilder, URI> uriFunction) {
        return executeRequest(HttpMethod.POST, uriFunction, hitDto, Object.class).block();
    }
}

