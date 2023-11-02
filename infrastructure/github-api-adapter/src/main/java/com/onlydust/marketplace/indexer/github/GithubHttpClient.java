package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.Optional;


@Slf4j
@AllArgsConstructor
public class GithubHttpClient {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Config config;

    public <T> T decodeBody(byte[] data, Class<T> classType) {
        try {
            return objectMapper.readValue(data, classType);
        } catch (IOException e) {
            throw OnlyDustException.internalServerError("Unable to deserialize github response", e);
        }
    }

    public HttpResponse<byte[]> fetch(String path) {
        return _fetch("GET", URI.create(config.baseUri + path), HttpRequest.BodyPublishers.noBody());
    }

    public HttpResponse<byte[]> fetch(URI uri) {
        return _fetch("GET", overrideHost(uri), HttpRequest.BodyPublishers.noBody());
    }


    private HttpResponse<byte[]> _fetch(String method, URI uri, HttpRequest.BodyPublisher bodyPublisher) {
        uri = overrideHost(uri);
        LOGGER.debug("Fetching {} {}", method, uri);
        try {
            final var requestBuilder = HttpRequest.newBuilder().uri(uri).headers("Authorization", "Bearer " + config.personalAccessToken).method(method, bodyPublisher);
            return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            throw OnlyDustException.internalServerError("Unable to fetch github API:" + uri, e);
        }
    }

    private URI overrideHost(URI uri) {
        final var baseUri = URI.create(config.baseUri);
        try {
            return new URI(
                    baseUri.getScheme(),
                    baseUri.getUserInfo(),
                    baseUri.getHost(),
                    baseUri.getPort(),
                    uri.getPath(),
                    uri.getQuery(),
                    uri.getFragment()
            );
        } catch (URISyntaxException e) {
            throw OnlyDustException.internalServerError("Unable to override host in URI", e);
        }
    }

    public <ResponseBody> Optional<ResponseBody> get(String path, Class<ResponseBody> responseClass) {
        final var httpResponse = fetch(path);
        return switch (httpResponse.statusCode()) {
            case 200 -> Optional.of(decodeBody(httpResponse.body(), responseClass));
            case 403, 404 -> Optional.empty();
            default ->
                    throw OnlyDustException.internalServerError("Received incorrect status (" + httpResponse.statusCode() + ") when fetching github API: " + path);
        };
    }

    public <ResponseBody> Optional<ResponseBody> graphql(String query, Object variables, Class<ResponseBody> responseClass) {
        try {
            final var body = Map.of("query", query, "variables", variables);
            final var httpResponse = _fetch("POST", URI.create(config.baseUri + "/graphql"), HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
            return switch (httpResponse.statusCode()) {
                case 200 -> Optional.of(decodeBody(httpResponse.body(), responseClass));
                case 403, 404 -> Optional.empty();
                default ->
                        throw OnlyDustException.internalServerError("Received incorrect status (" + httpResponse.statusCode() + ") when fetching github graphql API");
            };
        } catch (JsonProcessingException e) {
            throw OnlyDustException.internalServerError("Unable to serialize graphql request body", e);
        }
    }

    @ToString
    @Data
    public static class Config {
        private String baseUri;
        private String personalAccessToken;
    }
}
