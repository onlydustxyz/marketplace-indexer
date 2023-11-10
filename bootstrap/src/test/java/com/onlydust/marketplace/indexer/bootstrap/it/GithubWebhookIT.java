package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.OldRepoIndexesEntity;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTriggerEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.OldRepoIndexesEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobTriggerEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAppInstallationEntityRepository;
import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import com.onlydust.marketplace.indexer.rest.github.security.GithubSignatureVerifier;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import javax.transaction.Transactional;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Transactional
public class GithubWebhookIT extends IntegrationTest {
    final Long MARKETPLACE_FRONTEND_ID = 498695724L;
    @Autowired
    RepoIndexingJobTriggerEntityRepository repoIndexingJobTriggerRepository;
    @Autowired
    OldRepoIndexesEntityRepository oldRepoIndexesEntityRepository;
    @Autowired
    GithubWebhookRestApi.Config config;
    @Autowired
    GithubAccountEntityRepository githubAccountRepository;
    @Autowired
    GithubAppInstallationEntityRepository githubAppInstallationEntityRepository;

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
    @Order(1)
    void should_index_repository_upon_installation_created_event() throws URISyntaxException, IOException, InterruptedException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation_created.json").toURI()));

        // When
        final var response = post(event);

        // Then
        response.expectStatus().isOk();

        assertThat(repoIndexingJobTriggerRepository.findAll()).containsExactly(new RepoIndexingJobTriggerEntity(MARKETPLACE_FRONTEND_ID, 42952633L));
        assertThat(oldRepoIndexesEntityRepository.findAll()).containsExactly(new OldRepoIndexesEntity(MARKETPLACE_FRONTEND_ID));

        final var account = GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .name("OnlyDust")
                .build();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        assertThat(installations.get(0).getId()).isEqualTo(42952633L);
        assertThat(installations.get(0).getAccount()).isEqualTo(account);
        assertThat(installations.get(0).getRepos()).hasSize(1);
        assertThat(installations.get(0).getRepos().get(0).getId()).isEqualTo(MARKETPLACE_FRONTEND_ID);

        // Wait for the job to finish
        waitForJobToFinish(MARKETPLACE_FRONTEND_ID, 2, 2);

        assertThat(repoRepository.findAll()).hasSize(1);
        assertThat(pullRequestsRepository.findAll()).hasSize(2);
        assertThat(issuesRepository.findAll()).hasSize(2);
    }

    @Test
    @Order(2)
    void should_remove_repository_upon_installation_deleted_event() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation_deleted.json").toURI()));

        // When
        final var response = post(event);

        // Then
        response.expectStatus().isOk();

        // Job data preserved
        assertThat(repoIndexingJobTriggerRepository.findAll()).containsExactly(new RepoIndexingJobTriggerEntity(MARKETPLACE_FRONTEND_ID, null));
        assertThat(oldRepoIndexesEntityRepository.findAll()).containsExactly(new OldRepoIndexesEntity(MARKETPLACE_FRONTEND_ID));

        // Installation removed
        assertThat(githubAppInstallationEntityRepository.findAll()).isEmpty();

        // Indexed data preserved
        assertThat(githubAccountRepository.findAll()).contains(GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .name("OnlyDust")
                .build());
        assertThat(repoRepository.findAll()).hasSize(1);
        assertThat(pullRequestsRepository.findAll()).hasSize(2);
        assertThat(issuesRepository.findAll()).hasSize(2);
    }

    protected WebTestClient.ResponseSpec post(final String event) {
        return client.post().uri(getApiURI("/github-app/webhook")).header("X-GitHub-Event", "installation")
                .header("X-Hub-Signature-256", "sha256=" + GithubSignatureVerifier.hmac(event.getBytes(), config.secret))
                .bodyValue(event)
                .exchange();
    }
}
