package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.OldRepoIndexesEntity;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.OldRepoIndexesEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAppInstallationEntityRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.Comparator;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubInstallationEventsIT extends IntegrationTest {
    final Long MARKETPLACE_FRONTEND_ID = 498695724L;
    final Long CAIRO_STREAMS_ID = 493795808L;
    private final long INSTALLATION_ID = 42952633L;
    @Autowired
    RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
    @Autowired
    OldRepoIndexesEntityRepository oldRepoIndexesEntityRepository;
    @Autowired
    GithubAccountEntityRepository githubAccountRepository;
    @Autowired
    GithubAppInstallationEntityRepository githubAppInstallationEntityRepository;

    @Test
    void should_reject_upon_invalid_signature() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation/installation_created_new.json").toURI()));

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
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation/installation_created_old.json").toURI()));
        final long OLD_INSTALLATION_ID = 42952632L;

        // When
        processEvent(event, "installation");

        // Then
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
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation/installation_created_new.json").toURI()));

        // When
        processEvent(event, "installation");

        // Then
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
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation/installation_added.json").toURI()));

        // When
        processEvent(event, "installation_repositories");

        // Then
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
    @Order(6)
    void should_handle_installation_removed_events() throws URISyntaxException, IOException {
        // Given
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation/installation_removed.json").toURI()));

        // When
        processEvent(event, "installation_repositories");

        // Then
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
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation/installation_suspended.json").toURI()));

        // When
        processEvent(event, "installation");

        // Then
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
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation/installation_unsuspended.json").toURI()));

        // When
        processEvent(event, "installation");

        // Then
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
        final var event = Files.readString(Paths.get(this.getClass().getResource("/github/webhook/events/installation/installation_deleted.json").toURI()));

        // When
        processEvent(event, "installation");

        // Then
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
                .getResource("/github/webhook/events/installation/installation_added_to_update.json").toURI()));
        final var installationRemovedByRemovingSomeReposEvent = Files.readString(Paths.get(this.getClass()
                .getResource("/github/webhook/events/installation/installation_removed_by_removing_some_repos.json").toURI()));

        // When
        processEvent(installationAddedEvent, "installation");

        // Then
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
}
