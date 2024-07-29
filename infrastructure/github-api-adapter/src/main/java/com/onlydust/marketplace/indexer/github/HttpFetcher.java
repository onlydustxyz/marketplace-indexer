package com.onlydust.marketplace.indexer.github;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.RetryException;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


@Slf4j
@AllArgsConstructor
public class HttpFetcher implements Fetcher {
    private final HttpClient httpClient;

    @Override
    public HttpResponse<byte[]> fetch(HttpRequest request) {
        LOGGER.debug("Fetching {} {}", request.method(), request.uri());
        try {
            return httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
        } catch (IOException | InterruptedException e) {
            throw new RetryException("Unable to fetch GitHub API", e);
        }
    }
}
