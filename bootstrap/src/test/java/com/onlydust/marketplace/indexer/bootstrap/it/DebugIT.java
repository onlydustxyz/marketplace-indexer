package com.onlydust.marketplace.indexer.bootstrap.it;

import org.junit.jupiter.api.Test;

public class DebugIT extends IntegrationTest {

    @Test
    public void should_return_github_access_token() throws InterruptedException {
        client.get()
                .uri(getApiURI("/api/v1/debug/github-token", "installationId", "42952633"))
                .header("Api-Key", "BACKEND_API_KEY")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.token").isNotEmpty();
    }

    @Test
    public void should_not_return_github_access_token_when_api_key_is_missing() throws InterruptedException {
        client.get()
                .uri(getApiURI("/api/v1/debug/github-token", "installationId", "42952633"))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.token").doesNotExist();
    }
}
