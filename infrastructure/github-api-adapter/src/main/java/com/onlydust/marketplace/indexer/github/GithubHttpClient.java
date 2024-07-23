package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.internalServerError;
import static java.util.function.Function.identity;


@Slf4j
@AllArgsConstructor
public class GithubHttpClient {
    private final ObjectMapper objectMapper;
    private final Fetcher httpFetcher;
    private final GithubConfig config;
    private final GithubTokenProvider tokenProvider;

    public <T> T decodeBody(byte[] data, Class<T> classType) {
        try {
            return objectMapper.readValue(Sanitizer.sanitize(data), classType);
        } catch (IOException e) {
            throw internalServerError("Unable to deserialize github response", e);
        }
    }

    public <ResponseBody> Optional<ResponseBody> decode(HttpResponse<byte[]> response, Function<byte[], ResponseBody> bodyConsumer) {
        return switch (response.statusCode()) {
            case 200, 201 -> Optional.of(bodyConsumer.apply(response.body()));
            case 403, 404, 410, 422, 451 -> Optional.empty();
            default -> throw internalServerError("Received incorrect status (%s) when fetching github API".formatted(response.statusCode()));
        };
    }

    public HttpResponse<byte[]> fetch(String path) {
        return fetch("GET", path);
    }

    public HttpResponse<byte[]> fetch(String method, String path) {
        return fetch(method, URI.create(config.getBaseUri() + path), BodyPublishers.noBody());
    }

    public HttpResponse<byte[]> fetch(URI uri) {
        return fetch("GET", uri, BodyPublishers.noBody());
    }

    private HttpResponse<byte[]> fetch(String method, URI uri, HttpRequest.BodyPublisher bodyPublisher) {
        final var requestBuilder = HttpRequest.newBuilder().uri(overrideHost(uri)).method(method, bodyPublisher);

        tokenProvider.accessToken().ifPresent(token -> requestBuilder.header("Authorization", "Bearer " + token));

        return httpFetcher.fetch(requestBuilder.build());
    }

    private URI overrideHost(URI uri) {
        final var baseUri = URI.create(config.getBaseUri());
        try {
            return new URI(baseUri.getScheme(), baseUri.getUserInfo(), baseUri.getHost(), baseUri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw internalServerError("Unable to override host in URI", e);
        }
    }

    public <ResponseBody> Optional<ResponseBody> get(String path, Class<ResponseBody> responseClass) {
        final var httpResponse = fetch(path);
        return decode(httpResponse, body -> decodeBody(body, responseClass));
    }

    public Optional<byte[]> get(String path) {
        final var httpResponse = fetch(path);
        return decode(httpResponse, identity());
    }

    public <ResponseBody> Optional<ResponseBody> graphql(String query, Object variables, Class<ResponseBody> responseClass) {
        try {
            final var body = Map.of("query", query, "variables", variables);
            final var httpResponse = fetch("POST",
                    URI.create(config.getBaseUri() + "/graphql"),
                    BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
            return decode(httpResponse, b -> decodeBody(b, responseClass));
        } catch (JsonProcessingException e) {
            throw internalServerError("Unable to serialize graphql request body", e);
        }
    }

    public <ResponseBody> Optional<ResponseBody> post(String path, Class<ResponseBody> responseClass) {
        final var httpResponse = fetch("POST", path);
        return decode(httpResponse, body -> decodeBody(body, responseClass));
    }
}
