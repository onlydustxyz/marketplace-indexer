package com.onlydust.marketplace.indexer.github.adapters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.GithubTokenProvider;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;


@Slf4j
public class GithubAppContextAdapter implements GithubAppContext, GithubTokenProvider {
    private final GithubHttpClient client;
    private String accessToken;

    public GithubAppContextAdapter(GithubHttpClient client) {
        this.client = client;
    }

    @Override
    public void withGithubApp(final Long installationId, Runnable callback) {
        try {
            getInstallationToken(installationId).ifPresent(token -> accessToken = token);
            callback.run();
        } finally {
            accessToken = null;
        }
        callback.run();
    }

    @Override
    public Optional<String> accessToken() {
        return Optional.ofNullable(accessToken);
    }

    private Optional<String> getInstallationToken(Long installationId) {
        final var response = client.post("/app/installations/" + installationId + "/access_tokens", Response.class);
        return response.map(Response::getToken);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    static class Response {
        String token;
    }
}
