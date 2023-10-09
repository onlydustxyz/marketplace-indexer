package com.onlydust.marketplace.indexer.github;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.exception.NotFound;
import lombok.AllArgsConstructor;
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

    public <ResponseBody> Optional<ResponseBody> get(String uri, Class<ResponseBody> responseClass) {
        try {
            final var requestBuilder = HttpRequest.newBuilder(URI.create(uri)).GET();
            final var httpResponse = this.httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofByteArray());
            final int statusCode = httpResponse.statusCode();

            return switch (statusCode) {
                case 200 -> Optional.of(objectMapper.readValue(httpResponse.body(), responseClass));
                case 404 -> Optional.empty();
                default -> throw new NotFound("Unable to fetch github API:" + uri);
            };
        } catch (IOException | InterruptedException e) {
            throw new NotFound("Unable to fetch github API:" + uri, e);
        }
    }
}
