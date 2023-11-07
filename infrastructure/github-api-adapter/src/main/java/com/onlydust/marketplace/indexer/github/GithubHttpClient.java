package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dockerjava.zerodep.shaded.org.apache.hc.core5.http.HttpStatus;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import lombok.AllArgsConstructor;
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
    private final GithubConfig config;
    private final GithubTokenProvider tokenProvider;

    public <T> T decodeBody(byte[] data, Class<T> classType) {
        try {
            return objectMapper.readValue(Sanitizer.sanitize(data), classType);
        } catch (IOException e) {
            throw OnlyDustException.internalServerError("Unable to deserialize github response", e);
        }
    }

    public <ResponseBody> Optional<ResponseBody> decode(HttpResponse<byte[]> response, Class<ResponseBody> responseClass) {
        return switch (response.statusCode()) {
            case 200 -> Optional.of(decodeBody(response.body(), responseClass));
            case 403, 404, 422 -> Optional.empty();
            default ->
                    throw OnlyDustException.internalServerError("Received incorrect status (" + response.statusCode() + ") when fetching github API");
        };
    }

    public HttpResponse<byte[]> fetch(String path) {
        return fetch("GET", path);
    }

    public HttpResponse<byte[]> fetch(String method, String path) {
        return fetch("GET", URI.create(config.getBaseUri() + path), HttpRequest.BodyPublishers.noBody());
    }

    public HttpResponse<byte[]> fetch(URI uri) {
        return fetch("GET", uri, HttpRequest.BodyPublishers.noBody());
    }

    private HttpResponse<byte[]> fetch(String method, URI uri, HttpRequest.BodyPublisher bodyPublisher) {
        final var requestBuilder = HttpRequest.newBuilder()
                .uri(overrideHost(uri))
                .method(method, bodyPublisher);

        tokenProvider.accessToken().ifPresent(token -> requestBuilder.header("Authorization", "Bearer " + token));

        return fetch(requestBuilder.build());
    }

    private HttpResponse<byte[]> fetch(HttpRequest request) {
        for (var retryCount = 0; retryCount < config.getMaxRetries(); ++retryCount) {
            try {
                try {
                    LOGGER.info("Fetching {} {}", request.method(), request.uri());
                    final var response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
                    if (response.statusCode() == HttpStatus.SC_BAD_GATEWAY) {
                        throw new IOException("502 BAD GATEWAY received");
                    }
                    return response;
                } catch (IOException e) {
                    LOGGER.warn("Error while fetching github ({}), will retry in {}ms", request.uri(), config.getRetryInterval(), e);
                    Thread.sleep(config.getRetryInterval());
                    LOGGER.info("Retry {}/{}", retryCount + 1, config.getMaxRetries());
                }
            } catch (InterruptedException e) {
                throw OnlyDustException.internalServerError("Github fetch (" + request.uri() + ") interrupted", e);
            }
        }
        throw OnlyDustException.internalServerError("Unable to fetch Github (" + request.uri() + "). Max retry reached");
    }

    private URI overrideHost(URI uri) {
        final var baseUri = URI.create(config.getBaseUri());
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
        return decode(httpResponse, responseClass);
    }

    public <ResponseBody> Optional<ResponseBody> graphql(String query, Object variables, Class<ResponseBody> responseClass) {
        try {
            final var body = Map.of("query", query, "variables", variables);
            final var httpResponse = fetch("POST", URI.create(config.getBaseUri() + "/graphql"), HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)));
            return decode(httpResponse, responseClass);
        } catch (JsonProcessingException e) {
            throw OnlyDustException.internalServerError("Unable to serialize graphql request body", e);
        }
    }

    public <ResponseBody> Optional<ResponseBody> post(String path, Class<ResponseBody> responseClass) {
        final var httpResponse = fetch("POST", path);
        return decode(httpResponse, responseClass);
    }
}
