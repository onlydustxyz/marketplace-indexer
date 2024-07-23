package com.onlydust.marketplace.indexer.github;

import lombok.AllArgsConstructor;
import org.springframework.retry.RetryException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@AllArgsConstructor
public class RetriedOnErrorHttpFetcher implements Fetcher {
    private final Fetcher fetcher;

    @Override
    @Retryable(retryFor = RetryException.class, maxAttempts = 10, backoff = @Backoff(delay = 300, multiplier = 2, maxDelay = 5000))
    public HttpResponse<byte[]> fetch(HttpRequest request) {
        var response = fetcher.fetch(request);

        if (isServerError(response))
            throw new RetryException("Received %s status from GitHub API".formatted(response.statusCode()));

        return response;
    }

    private boolean isServerError(HttpResponse<?> httpResponse) {
        return httpResponse.statusCode() >= 500;
    }
}
