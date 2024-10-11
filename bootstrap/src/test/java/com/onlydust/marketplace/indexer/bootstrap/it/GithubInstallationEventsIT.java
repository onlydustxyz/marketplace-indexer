package com.onlydust.marketplace.indexer.bootstrap.it;

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
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.EventsInboxEntityRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

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
    GithubRepoEntityRepository githubRepoEntityRepository;
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
                .usingFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(new RepoIndexingJobEntity(CAIRO_STREAMS_ID, OLD_INSTALLATION_ID, false, true),
                        new RepoIndexingJobEntity(MARKETPLACE_FRONTEND_ID, OLD_INSTALLATION_ID, false, true));

        final var account = GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .build();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        assertThat(installations.get(0).id()).isEqualTo(OLD_INSTALLATION_ID);
        assertThat(installations.get(0).account()).isEqualToComparingFieldByField(account);
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
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(null);
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isEqualTo(INSTALLATION_ID);

        final var account = GithubAccountEntity.builder()
                .id(98735558L)
                .login("onlydustxyz")
                .type(GithubAccountEntity.Type.ORGANIZATION)
                .avatarUrl("https://avatars.githubusercontent.com/u/98735558?v=4")
                .htmlUrl("https://github.com/onlydustxyz")
                .build();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        assertThat(installations.get(0).id()).isEqualTo(INSTALLATION_ID);
        assertThat(installations.get(0).account()).isEqualToComparingFieldByField(account);

        final var repos = installations.get(0).repos();
        assertThat(repos).hasSize(1);
        assertThat(repos.get(0).getId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
    }

    @Test
    @Order(3)
    void should_handle_installation_added_events() {
        // When
        processEventsFromPaths("installation_repositories", "/github/webhook/events/installation/installation_added.json");

        // Then
        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isEqualTo(INSTALLATION_ID);

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
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isNull();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).repos();
        assertThat(repos).hasSize(1);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
    }

    @Test
    @Order(7)
    void should_handle_installation_suspended_event() {
        // When
        processEventsFromPaths("installation", "/github/webhook/events/installation/installation_suspended.json");

        // Then
        final var jobs = repoIndexingJobEntityRepository.findAll(Sort.by("repoId"));
        assertThat(jobs).hasSize(2);
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(0).getInstallationSuspendedAt().toInstant()).isEqualTo(ZonedDateTime.parse("2023-11-13T14:21:39Z").toInstant());
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isNull();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).repos();
        assertThat(repos).hasSize(1);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
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
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isEqualTo(INSTALLATION_ID);
        assertThat(jobs.get(0).getInstallationSuspendedAt()).isNull();
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isNull();

        final var installations = githubAppInstallationEntityRepository.findAll();
        assertThat(installations).hasSize(1);
        final var repos = installations.get(0).repos();
        assertThat(repos).hasSize(1);
        assertThat(repos.get(0).getId()).isEqualTo(CAIRO_STREAMS_ID);
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
        assertThat(jobs.get(0).getRepoId()).isEqualTo(CAIRO_STREAMS_ID);
        assertThat(jobs.get(0).getInstallationId()).isNull();
        assertThat(jobs.get(1).getRepoId()).isEqualTo(MARKETPLACE_FRONTEND_ID);
        assertThat(jobs.get(1).getInstallationId()).isNull();

        // Installation removed
        assertThat(githubAppInstallationEntityRepository.findAll()).isEmpty();

        // Indexed data preserved
        assertThat(githubAccountRepository.findAll()).hasSize(1);
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
        assertThat(githubRepoEntityRepository.findById(493795809L)).isPresent();
        assertThat(githubRepoEntityRepository.findAll().stream().filter(r -> r.getOwnerLogin().equals("onlydustxyz") && r.getName().equals("cairo-streams"))).hasSize(2);
    }

    @Test
    @Order(12)
    void failed_installation_should_not_block_others() throws JsonProcessingException {
        // Given
        eventsInboxEntityRepository.saveAll(List.of(new EventsInboxEntity("installation", mapper.readTree("{\"installation\":{\"id\": 1}," +
                                                                                                          "\"action\":\"remove\"}")).failed("unable to " +
                                                                                                                                            "process"),
                new EventsInboxEntity("installation", mapper.readTree("{\"installation\":{\"id\": 1},\"action\":\"remove\"}")), new EventsInboxEntity(
                        "installation", mapper.readTree("{\"installation\":{\"id\": 2},\"action\":\"remove\"}"))));

        // When
        installationEventsInboxJob.run();

        // Then
        final var processed =
                eventsInboxEntityRepository.findAll().stream().filter(e -> e.getPayload().at("/installation/id").asInt() == 2).findFirst().orElseThrow();
        assertThat(processed.getStatus()).isEqualTo(EventsInboxEntity.Status.PROCESSED);
        final var notProcessed = eventsInboxEntityRepository.findAll().stream().filter(e -> e.getPayload().at("/installation/id").asInt() == 1).toList();
        assertThat(notProcessed).hasSize(2);
        assertThat(notProcessed.get(0).getStatus()).isEqualTo(EventsInboxEntity.Status.FAILED);
        assertThat(notProcessed.get(1).getStatus()).isEqualTo(EventsInboxEntity.Status.PROCESSED);
    }
}
