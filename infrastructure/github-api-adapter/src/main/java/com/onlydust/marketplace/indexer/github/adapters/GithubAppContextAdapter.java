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
    private final ThreadLocal<Long> installationId = new ThreadLocal<>();
    private final ThreadLocal<String> accessToken = new ThreadLocal<>();

    public GithubAppContextAdapter(GithubHttpClient client) {
        this.client = client;
    }

    @Override
    public void withGithubApp(final Long installationId, Runnable callback) {
        try {
            this.installationId.set(installationId);
            getInstallationToken(installationId).ifPresent(accessToken::set);
            callback.run();
        } finally {
            this.installationId.remove();
            accessToken.remove();
        }
    }

    @Override
    public Optional<Long> installationId() {
        return Optional.ofNullable(installationId.get());
    }

    @Override
    public Optional<String> accessToken() {
        return Optional.ofNullable(accessToken.get());
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
