package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;


@Slf4j
@AllArgsConstructor
public class GithubHttpClient {
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private final Config config;

    public <T> T decode(byte[] data, Class<T> classType) {
        try {
            return objectMapper.readValue(data, classType);
        } catch (IOException e) {
            throw new NotFound("Unable to deserialize github response", e);
        }
    }

    public HttpResponse<byte[]> fetch(final URI uri) {
        LOGGER.debug("Fetching {}", uri);
        try {
            final var requestBuilder = HttpRequest.newBuilder().uri(uri).headers("Authorization", "Bearer " + config.personalAccessToken).GET();
            return httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            throw new NotFound("Unable to fetch github API:" + uri, e);
        }
    }

    public <ResponseBody> Optional<ResponseBody> get(String path, Class<ResponseBody> responseClass) {
        final var httpResponse = fetch(buildURI(path));
        return switch (httpResponse.statusCode()) {
            case 200 -> Optional.of(decode(httpResponse.body(), responseClass));
            case 403, 404 -> Optional.empty();
            default -> throw new NotFound("Unable to fetch github API: " + path);
        };
    }

    public final URI buildURI(String path) {
        return URI.create(config.baseUri + path);
    }

    @ToString
    @Data
    public static class Config {
        private String baseUri;
        private String personalAccessToken;
    }
}
