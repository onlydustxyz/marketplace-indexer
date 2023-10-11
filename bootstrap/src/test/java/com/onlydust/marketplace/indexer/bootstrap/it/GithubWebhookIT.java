package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import com.onlydust.marketplace.indexer.rest.github.security.GithubSignatureVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GithubWebhookIT extends IntegrationTest {
    @Autowired
    GithubWebhookRestApi.Config config;

    @Test
    void should_reject_upon_invalid_signature() {
        // Given
        final String event = this.getClass().getResourceAsStream("/github/webhook/events/new_installation.json").toString();

        // When
        final var response = post("/github-app/webhook")
                .header("X-GitHub-Event", "installation")
                .header("X-Hub-Signature-256", "sha256=invalid")
                .bodyValue(event)
                .exchange();

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    void should_accept_upon_valid_signature() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/new_installation.json").toURI()));

        // When
        final var response = post("/github-app/webhook")
                .header("X-GitHub-Event", "installation")
                .header("X-Hub-Signature-256", "sha256=" + GithubSignatureVerifier.hmac(event.getBytes(), config.secret))
                .bodyValue(event)
                .exchange();

        // Then
        response.expectStatus().isOk();
    }

    protected WebTestClient.RequestBodySpec post(final String path) {
        return client.post().uri(getApiURI(path));
    }
}
