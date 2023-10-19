package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTriggerEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobTriggerEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountRepository;
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
    RepoIndexingJobTriggerEntityRepository repoIndexingJobTriggerRepository;
    @Autowired
    GithubWebhookRestApi.Config config;
    @Autowired
    GithubAccountRepository githubAccountRepository;

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

        assertThat(repoIndexingJobTriggerRepository.findAll()).containsExactly(new RepoIndexingJobTriggerEntity(42952633L, MARKETPLACE_FRONTEND_ID));
        assertThat(githubAccountRepository.findAll()).containsExactly(GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .installationId(42952633L)
                .build());
    }

    @Test
    void should_remove_repository_upon_installation_deleted_event() throws URISyntaxException, IOException {
        // Given
        repoIndexingJobTriggerRepository.save(new RepoIndexingJobTriggerEntity(42952633L, MARKETPLACE_FRONTEND_ID));
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation_deleted.json").toURI()));

        // When
        final var response = post(event);

        // Then
        response.expectStatus().isOk();

        assertThat(repoIndexingJobTriggerRepository.findAll()).isEmpty();
        assertThat(githubAccountRepository.findAll()).containsExactly(GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .installationId(null)
                .build());
    }

    protected WebTestClient.ResponseSpec post(final String event) {
        return client.post().uri(getApiURI("/github-app/webhook")).header("X-GitHub-Event", "installation")
                .header("X-Hub-Signature-256", "sha256=" + GithubSignatureVerifier.hmac(event.getBytes(), config.secret))
                .bodyValue(event)
                .exchange();
    }
}
