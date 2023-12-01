package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.jobs.EventsInboxJob;
import com.onlydust.marketplace.indexer.postgres.entities.OldRepoIndexesEntity;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.OldRepoIndexesEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAppInstallationEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import com.onlydust.marketplace.indexer.rest.github.security.GithubSignatureVerifier;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubWebhookIT extends IntegrationTest {
    final Long MARKETPLACE_FRONTEND_ID = 498695724L;
    final Long CAIRO_STREAMS_ID = 493795808L;
    private final long INSTALLATION_ID = 42952633L;
    @Autowired
    public RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
    @Autowired
    OldRepoIndexesEntityRepository oldRepoIndexesEntityRepository;
    @Autowired
    GithubWebhookRestApi.Config config;
    @Autowired
    GithubAccountEntityRepository githubAccountRepository;
    @Autowired
    GithubAppInstallationEntityRepository githubAppInstallationEntityRepository;
    @Autowired
    RepoContributorRepository repoContributorRepository;
    @Autowired
    GithubRepoEntityRepository githubRepoRepository;
    @Autowired
    EventsInboxJob eventsInboxJob;

    @Test
    void should_reject_upon_invalid_signature() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events" +
                                                                                 "/installation_created_new.json").toURI()));

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
    void should_handle_installation_created_event() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events" +
                                                                                 "/installation_created_old.json").toURI()));
        final long OLD_INSTALLATION_ID = 42952632L;

        processEvent(event, "installation");

        assertThat(repoIndexingJobEntityRepository.findAll()).containsExactlyInAnyOrder(
                new RepoIndexingJobEntity(CAIRO_STREAMS_ID, OLD_INSTALLATION_ID, false, true),
                new RepoIndexingJobEntity(MARKETPLACE_FRONTEND_ID, OLD_INSTALLATION_ID, false, true)
        );
        assertThat(oldRepoIndexesEntityRepository.findAll()).containsExactlyInAnyOrder(
                new OldRepoIndexesEntity(CAIRO_STREAMS_ID),
                new OldRepoIndexesEntity(MARKETPLACE_FRONTEND_ID)
        );

        final var account = GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .build();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        assertThat(installations.get(0).getId()).isEqualTo(OLD_INSTALLATION_ID);
        assertThat(installations.get(0).getAccount()).isEqualTo(account);

        final var repos =
                installations.get(0).getRepos().stream().sorted(Comparator.comparing(GithubRepoEntity::getId)).toList();
        assertThat(repos).hasSize(2);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(repos.get(1).getId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
    }

    @Test
    @Order(2)
    void should_handle_duplicate_installation_created_event() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events" +
                                                                                 "/installation_created_new.json").toURI()));

        // When
        processEvent(event, "installation");

        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(null);
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isEqualTo(INSTALLATION_ID);

        assertThat(oldRepoIndexesEntityRepository.findAll()).containsExactlyInAnyOrder(
                new OldRepoIndexesEntity(CAIRO_STREAMS_ID),
                new OldRepoIndexesEntity(MARKETPLACE_FRONTEND_ID)
        );

        final var account = GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .build();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        assertThat(installations.get(0).getId()).isEqualTo(INSTALLATION_ID);
        assertThat(installations.get(0).getAccount()).isEqualTo(account);

        final var repos = installations.get(0).getRepos();
        assertThat(repos).hasSize(1);
        assertThat(repos.get(0).getId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
    }

    @Test
    @Order(3)
    void should_handle_installation_added_events() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events" +
                                                                                 "/installation_added.json").toURI()));

        // When
        processEvent(event, "installation_repositories");

        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isEqualTo(INSTALLATION_ID);

        assertThat(oldRepoIndexesEntityRepository.findAll()).containsExactlyInAnyOrder(
                new OldRepoIndexesEntity(CAIRO_STREAMS_ID),
                new OldRepoIndexesEntity(MARKETPLACE_FRONTEND_ID)
        );

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos =
                installations.get(0).getRepos().stream().sorted(Comparator.comparing(GithubRepoEntity::getId)).toList();
        assertThat(repos).hasSize(2);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(repos.get(1).getId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
    }

    @Test
    @Order(4)
    void should_handle_repo_becoming_private() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/cairo-streams-privatized.json").toURI()));

        // When
        processEvent(event, "repository");

        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getIsPublic()).isFalse();
        assertThat(oldRepoIndexesEntityRepository.findById(CAIRO_STREAMS_ID)).isEmpty();
        assertThat(githubRepoRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getVisibility()).isEqualTo(GithubRepoEntity.Visibility.PRIVATE);
    }

    @Test
    @Order(5)
    void should_handle_repo_becoming_public() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/cairo-streams-publicized.json").toURI()));

        // When
        processEvent(event, "repository");

        assertThat(repoIndexingJobEntityRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getIsPublic()).isTrue();
        assertThat(oldRepoIndexesEntityRepository.findById(CAIRO_STREAMS_ID)).isPresent();
        assertThat(githubRepoRepository.findById(CAIRO_STREAMS_ID).orElseThrow().getVisibility()).isEqualTo(GithubRepoEntity.Visibility.PUBLIC);
    }

    @Test
    @Order(6)
    void should_handle_installation_removed_events() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events" +
                                                                                 "/installation_removed.json").toURI()));

        // When
        processEvent(event, "installation_repositories");

        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isNull();

        assertThat(oldRepoIndexesEntityRepository.findAll()).containsExactlyInAnyOrder(
                new OldRepoIndexesEntity(MARKETPLACE_FRONTEND_ID),
                new OldRepoIndexesEntity(CAIRO_STREAMS_ID)
        );

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).getRepos();
        assertThat(repos).hasSize(1);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
    }

    @Test
    @Order(7)
    void should_handle_installation_suspended_event() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events" +
                                                                                 "/installation_suspended.json").toURI()));

        // When
        processEvent(event, "installation");

        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(0).getSuspendedAt().toInstant()).isEqualTo(ZonedDateTime.parse("2023-11-13T14:21:39Z").toInstant());
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isNull();

        assertThat(oldRepoIndexesEntityRepository.findAll()).containsExactlyInAnyOrder(
                new OldRepoIndexesEntity(MARKETPLACE_FRONTEND_ID),
                new OldRepoIndexesEntity(CAIRO_STREAMS_ID)
        );

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).getRepos();
        assertThat(repos).hasSize(1);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(installations.get(0).getSuspendedAt().toInstant()).isEqualTo(ZonedDateTime.parse("2023-11-13T14:21:39Z").toInstant());
    }


    @Test
    @Order(8)
    void should_handle_installation_unsuspended_event() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events" +
                                                                                 "/installation_unsuspended.json").toURI()));

        // When
        processEvent(event, "installation");

        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(0).getSuspendedAt()).isNull();
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isNull();

        assertThat(oldRepoIndexesEntityRepository.findAll()).containsExactlyInAnyOrder(
                new OldRepoIndexesEntity(MARKETPLACE_FRONTEND_ID),
                new OldRepoIndexesEntity(CAIRO_STREAMS_ID)
        );

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).getRepos();
        assertThat(repos).hasSize(1);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
    }

    @Test
    @Order(9)
    void should_handle_installation_deleted_event() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events" +
                                                                                 "/installation_deleted.json").toURI()));

        // When
        processEvent(event, "installation");

        // Job data preserved
        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isNull();
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isNull();

        assertThat(oldRepoIndexesEntityRepository.findAll()).containsExactlyInAnyOrder(
                new OldRepoIndexesEntity(MARKETPLACE_FRONTEND_ID),
                new OldRepoIndexesEntity(CAIRO_STREAMS_ID)
        );

        // Installation removed
        assertThat(githubAppInstallationEntityRepository.findAll()).isEmpty();

        // Indexed data preserved
        assertThat(githubAccountRepository.findAll()).hasSize(1);
    }

    @Test
    @Order(10)
    void should_handle_installation_remove_some_repos() throws URISyntaxException, IOException {
        // Given
        final var installationAddedEvent = Files.readString(Paths.get(this.getClass()
                .getResource("/github/webhook/events/installation_added_to_update.json").toURI()));
        final var installationRemovedByRemovingSomeReposEvent = Files.readString(Paths.get(this.getClass()
                .getResource("/github/webhook/events/installation_removed_by_removing_some_repos.json").toURI()));

        // When
        processEvent(installationAddedEvent, "installation");

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos =
                installations.get(0).getRepos().stream().sorted(Comparator.comparing(GithubRepoEntity::getId)).toList();
        assertThat(repos).hasSize(2);
        assertThat(repos.get(0).getId()).isEqualTo(715033198);
        assertThat(repos.get(1).getId()).isEqualTo(715033315);

        // When
        processEvent(installationRemovedByRemovingSomeReposEvent, "installation_repositories");

        // Then
        final var installationsUpdated = githubAppInstallationEntityRepository.findAll();
        assertThat(installationsUpdated).hasSize(1);
        final var reposUpdated =
                installationsUpdated.get(0).getRepos().stream().sorted(Comparator.comparing(GithubRepoEntity::getId)).toList();
        assertThat(reposUpdated).hasSize(1);
        assertThat(reposUpdated.get(0).getId()).isEqualTo(715033198);
    }

    protected WebTestClient.ResponseSpec post(final String event, String eventTypeHeader) {
        return client.post().uri(getApiURI("/github-app/webhook")).header("X-GitHub-Event", eventTypeHeader)
                .header("X-Hub-Signature-256", "sha256=" + GithubSignatureVerifier.hmac(event.getBytes(),
                        config.secret))
                .bodyValue(event)
                .exchange();
    }

    private void processEvent(String event, String installation) {
        post(event, installation).expectStatus().isOk();
        eventsInboxJob.run();
    }
}
