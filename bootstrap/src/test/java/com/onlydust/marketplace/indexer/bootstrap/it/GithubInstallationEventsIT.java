package com.onlydust.marketplace.indexer.bootstrap.it;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.postgres.entities.EventsInboxEntity;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAppInstallationEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.EventsInboxEntityRepository;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GithubInstallationEventsIT extends IntegrationTest {
    final Long MARKETPLACE_FRONTEND_ID = 498695724L;
    final Long CAIRO_STREAMS_ID = 493795808L;
    final ObjectMapper objectMapper = new ObjectMapper();
    private final long INSTALLATION_ID = 42952633L;
    @Autowired
    public JobManager diffRepoRefreshJobManager;
    @Autowired
    RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
    @Autowired
    GithubAccountEntityRepository githubAccountRepository;
    @Autowired
    GithubAppInstallationEntityRepository githubAppInstallationEntityRepository;
    @Autowired
    GithubRepoRepository githubRepoRepository;
    @Autowired
    EventsInboxEntityRepository eventsInboxEntityRepository;

    @Test
    void should_reject_upon_invalid_signature() {
        // Given
        final var event = "/github/webhook/events/installation/installation_created_new.json";

        // When
        final var response = client.post()
                .uri(getApiURI("/github-app/webhook"))
                .header("X-GitHub-Event", "installation")
                .header("X-Hub-Signature-256", "sha256=invalid")
                .bodyValue(event)
                .exchange();

        // Then
        response.expectStatus().isUnauthorized();
    }

    @Test
    @Order(1)
    void should_handle_installation_created_event() {
        // Given
        final long OLD_INSTALLATION_ID = 42952632L;

        // When
        processEventsFromPaths("installation", "/github/webhook/events/installation/installation_created_old.json");

        // Then
        assertThat(repoIndexingJobEntityRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(new RepoIndexingJobEntity(CAIRO_STREAMS_ID, OLD_INSTALLATION_ID, false, true),
                        new RepoIndexingJobEntity(MARKETPLACE_FRONTEND_ID, OLD_INSTALLATION_ID, false, true));

        final var account = GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .followerCount(0)
                .build();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        assertThat(installations.get(0).id()).isEqualTo(OLD_INSTALLATION_ID);
        assertThat(installations.get(0).account()).usingRecursiveComparison().isEqualTo(account);
        assertThat(installations.get(0).permissions()).containsExactlyInAnyOrder("issues:read", "metadata:read", "pull_requests:read");

        final var repos = installations.get(0).repos().stream().sorted(Comparator.comparing(GithubRepoEntity::getId)).toList();
        assertThat(repos).hasSize(2);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(repos.get(1).getId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
    }

    @Test
    @Order(2)
    void should_handle_duplicate_installation_created_event() {
        // When
        processEventsFromPaths("installation", "/github/webhook/events/installation/installation_created_new.json");

        // Then
        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).repoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).installationId()).isEqualTo(null);
        assertThat(jobs.get(1).repoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).installationId()).isEqualTo(INSTALLATION_ID);

        final var account = GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .followerCount(0)
                .build();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        assertThat(installations.get(0).id()).isEqualTo(INSTALLATION_ID);
        assertThat(installations.get(0).account()).usingRecursiveComparison().isEqualTo(account);

        final var repos = installations.get(0).repos();
        assertThat(repos).hasSize(1);
        assertThat(repos.stream().findFirst().orElseThrow().getId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
    }

    @Test
    @Order(3)
    void should_handle_installation_added_events() {
        // When
        processEventsFromPaths("installation_repositories", "/github/webhook/events/installation/installation_added.json");

        // Then
        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).repoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).installationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(1).repoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).installationId()).isEqualTo(INSTALLATION_ID);

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).repos().stream().sorted(Comparator.comparing(GithubRepoEntity::getId)).toList();
        assertThat(repos).hasSize(2);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(repos.get(1).getId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
    }

    @Test
    @Order(6)
    void should_handle_installation_removed_events() {
        // When
        processEventsFromPaths("installation_repositories", "/github/webhook/events/installation/installation_removed.json");

        // Then
        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).repoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).installationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(1).repoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).installationId()).isNull();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).repos();
        assertThat(repos).hasSize(1);
        assertThat(repos.stream().findFirst().orElseThrow().getId()).isEqualTo(CAIRO_STREAMS_ID);
    }

    @Test
    @Order(7)
    void should_handle_installation_suspended_event() {
        // When
        processEventsFromPaths("installation", "/github/webhook/events/installation/installation_suspended.json");

        // Then
        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).repoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).installationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(0).installationSuspendedAt().toInstant()).isEqualTo(ZonedDateTime.parse("2023-11-13T14:21:39Z").toInstant());
        assertThat(jobs.get(1).repoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).installationId()).isNull();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).repos();
        assertThat(repos).hasSize(1);
        assertThat(repos.stream().findFirst().orElseThrow().getId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(installations.get(0).suspendedAt().toInstant()).isEqualTo(ZonedDateTime.parse("2023-11-13T14:21:39Z").toInstant());
    }

    @Test
    @Order(8)
    void should_handle_installation_unsuspended_event() {
        // When
        processEventsFromPaths("installation", "/github/webhook/events/installation/installation_unsuspended.json");

        // Then
        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).repoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).installationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(0).installationSuspendedAt()).isNull();
        assertThat(jobs.get(1).repoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).installationId()).isNull();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).repos();
        assertThat(repos).hasSize(1);
        assertThat(repos.stream().findFirst().orElseThrow().getId()).isEqualTo(CAIRO_STREAMS_ID);
    }

    @Test
    @Order(8)
    void should_handle_installation_new_permissions_accepted_event() {
        // When
        processEventsFromPaths("installation", "/github/webhook/events/installation/new_permissions_accepted.json");

        // Then
        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        assertThat(installations.get(0).permissions()).containsExactlyInAnyOrder("issues:read", "issues:write", "metadata:read", "pull_requests:read");
    }

    @Test
    @Order(9)
    void should_handle_installation_deleted_event() {
        // When
        processEventsFromPaths("installation", "/github/webhook/events/installation/installation_deleted.json");

        // Then
        // Job data preserved
        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).repoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).installationId()).isNull();
        assertThat(jobs.get(1).repoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).installationId()).isNull();

        // Installation removed
        assertThat(githubAppInstallationEntityRepository.findAll()).isEmpty();

        // Indexed data preserved
        assertThat(githubAccountRepository.count()).isEqualTo(1);
    }

    @Test
    @Order(10)
    void should_handle_installation_remove_some_repos() {
        // When
        processEventsFromPaths("installation", "/github/webhook/events/installation/installation_added_to_update.json");

        // Then
        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).repos().stream().sorted(Comparator.comparing(GithubRepoEntity::getId)).toList();
        assertThat(repos).hasSize(2);
        assertThat(repos.get(0).getId()).isEqualTo(715033198);
        assertThat(repos.get(1).getId()).isEqualTo(715033315);

        // When
        processEventsFromPaths("installation_repositories", "/github/webhook/events/installation/installation_removed_by_removing_some_repos.json");

        // Then
        final var installationsUpdated = githubAppInstallationEntityRepository.findAll();
        assertThat(installationsUpdated).hasSize(1);
        final var reposUpdated = installationsUpdated.get(0).repos().stream().sorted(Comparator.comparing(GithubRepoEntity::getId)).toList();
        assertThat(reposUpdated).hasSize(1);
        assertThat(reposUpdated.get(0).getId()).isEqualTo(715033198);
    }

    @Test
    @Order(11)
    void should_handle_duplicate_repo_added() {
        // Given
        processEventsFromPaths("installation",
                "/github/webhook/events/installation/installation_created_new.json",
                "/github/webhook/events/installation/installation_added.json");

        diffRepoRefreshJobManager.createJob().run();

        // When
        processEventsFromPaths("installation", "/github/webhook/events/installation/installation_added.json");

        // Then
        final var installation = githubAppInstallationEntityRepository.findById(INSTALLATION_ID).orElseThrow();
        assertThat(installation.repos()).hasSize(2);
    }

    @Test
    @Order(12)
    void should_handle_recreating_same_repo_with_different_id() {
        // When
        processEventsFromPaths("installation_repositories",
                "/github/webhook/events/installation/installation_added_with_another_repo_id.json");

        diffRepoRefreshJobManager.createJob().run();

        // Then
        final var installation = githubAppInstallationEntityRepository.findById(INSTALLATION_ID).orElseThrow();
        assertThat(installation.repos()).hasSize(3);
        assertThat(githubRepoRepository.findById(493795809L)).isPresent();
        assertThat(githubRepoRepository.findAll().stream().filter(r -> r.getOwnerLogin().equals("onlydustxyz") && r.getName().equals("cairo-streams"))).hasSize(2);
    }

    @Test
    @Order(12)
    void failed_installation_should_not_block_others() throws JsonProcessingException {
        // Given
        eventsInboxEntityRepository.persistAll(List.of(new EventsInboxEntity("installation", mapper.readTree("{\"installation\":{\"id\": 1}," +
                                                                                                             "\"action\":\"remove\"}")).failed("unable to " +
                                                                                                                                               "process"),
                new EventsInboxEntity("installation", mapper.readTree("{\"installation\":{\"id\": 1},\"action\":\"remove\"}")), new EventsInboxEntity(
                        "installation", mapper.readTree("{\"installation\":{\"id\": 2},\"action\":\"remove\"}"))));

        // When
        installationEventsInboxJob.run();

        // Then
        final var processed =
                eventsInboxEntityRepository.findAll().stream().filter(e -> e.payload().at("/installation/id").asInt() == 2).findFirst().orElseThrow();
        assertThat(processed.status()).isEqualTo(EventsInboxEntity.Status.PROCESSED);
        final var notProcessed = eventsInboxEntityRepository.findAll().stream().filter(e -> e.payload().at("/installation/id").asInt() == 1).toList();
        assertThat(notProcessed).hasSize(2);
        assertThat(notProcessed.get(0).status()).isEqualTo(EventsInboxEntity.Status.FAILED);
        assertThat(notProcessed.get(1).status()).isEqualTo(EventsInboxEntity.Status.PROCESSED);
    }
}
