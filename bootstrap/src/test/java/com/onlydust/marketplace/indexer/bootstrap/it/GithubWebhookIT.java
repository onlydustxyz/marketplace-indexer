package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobTriggerRepository;
import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import com.onlydust.marketplace.indexer.rest.github.security.GithubSignatureVerifier;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

public class GithubWebhookIT extends IntegrationTest {
    final Long MARKETPLACE_FRONTEND_ID = 498695724L;
    @Autowired
    public RepoIndexingJobTriggerRepository repoIndexingJobTriggerRepository;
    @Autowired
    GithubWebhookRestApi.Config config;

    @Test
    void should_reject_upon_invalid_signature() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation_created.json").toURI()));

        // When
        final var response = client.post().uri(getApiURI("/github-app/webhook"))
                .header("X-GitHub-Event", "installation")
                .header("X-Hub-Signature-256", "sha256=invalid")
                .bodyValue(event)
                .exchange();

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    void should_index_repository_upon_installation_created_event() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation_created.json").toURI()));

        // When
        final var response = post(event);

        // Then
        response.expectStatus().isOk();

        assertThat(repoIndexingJobTriggerRepository.list()).containsExactly(new RepoIndexingJobTrigger(42952633L, MARKETPLACE_FRONTEND_ID));
    }

    @Test
    void should_remove_repository_upon_installation_deleted_event() throws URISyntaxException, IOException {
        // Given
        repoIndexingJobTriggerRepository.add(new RepoIndexingJobTrigger(42952633L, MARKETPLACE_FRONTEND_ID));
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation_deleted.json").toURI()));

        // When
        final var response = post(event);

        // Then
        response.expectStatus().isOk();

        assertThat(repoIndexingJobTriggerRepository.list()).isEmpty();
    }

    protected WebTestClient.ResponseSpec post(final String event) {
        return client.post().uri(getApiURI("/github-app/webhook")).header("X-GitHub-Event", "installation")
                .header("X-Hub-Signature-256", "sha256=" + GithubSignatureVerifier.hmac(event.getBytes(), config.secret))
                .bodyValue(event)
                .exchange();
    }
}
