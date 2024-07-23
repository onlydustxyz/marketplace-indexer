package com.onlydust.marketplace.indexer.github;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.internalServerError;


@Slf4j
@AllArgsConstructor
public class RedirectionHandledHttpFetcher implements Fetcher {
    private final Fetcher fetcher;
    private final GithubConfig config;

    @Override
    public HttpResponse<byte[]> fetch(HttpRequest request) {
        final var response = fetcher.fetch(request);
        // Use the fetcher to follow redirects to avoid following infinite redirections
        return isRedirect(response) ? fetcher.fetch(redirect(response)) : response;
    }

    private HttpRequest redirect(HttpResponse<byte[]> response) {
        final var location = response.headers().firstValue("Location")
                .orElseThrow(() -> internalServerError("Redirect without location header"));

        return HttpRequest.newBuilder(response.request(), (n, v) -> true)
                .uri(overrideHost(URI.create(location)))
                .build();
    }

    private boolean isRedirect(HttpResponse<?> httpResponse) {
        return httpResponse.statusCode() >= 300 && httpResponse.statusCode() < 400;
    }

    private URI overrideHost(URI uri) {
        final var baseUri = URI.create(config.getBaseUri());
        try {
            return new URI(baseUri.getScheme(), baseUri.getUserInfo(), baseUri.getHost(), baseUri.getPort(), uri.getPath(), uri.getQuery(), uri.getFragment());
        } catch (URISyntaxException e) {
            throw internalServerError("Unable to override host in URI", e);
        }
    }
}
