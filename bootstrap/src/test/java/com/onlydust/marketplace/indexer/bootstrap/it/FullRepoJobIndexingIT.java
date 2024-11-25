package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoLanguageEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.RepoContributorEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.ApiEventRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.*;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.IssueRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.PullRequestRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Comparator.comparing;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FullRepoJobIndexingIT extends IntegrationTest {
    final static Long MARKETPLACE = 498695724L;
    final static Long BRETZEL_APP = 380954304L;

    @Autowired
    public ContributionRepository contributionRepository;
    @Autowired
    public GroupedContributionRepository groupedContributionRepository;
    @Autowired
    public RepoContributorRepository repoContributorRepository;
    @Autowired
    public PullRequestRepository pullRequestsRepository;
    @Autowired
    public IssueRepository issuesRepository;
    @Autowired
    public GithubRepoRepository githubRepoRepository;
    @Autowired
    public GithubRepoStatsEntityRepository githubRepoStatsEntityRepository;
    @Autowired
    public RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
    @Autowired
    public JobManager diffRepoRefreshJobManager;
    @Autowired
    public ApiEventRepository apiEventRepository;

    private WebTestClient.ResponseSpec onRepoLinkChanged(List<Long> linkedRepoIds, List<Long> unlinkedRepoIds) {
        return post("/api/v1/events/on-repo-link-changed", """
                {
                    "linkedRepoIds": %s,
                    "unlinkedRepoIds": %s
                }
                """.formatted(Arrays.toString(linkedRepoIds.toArray()), Arrays.toString(unlinkedRepoIds.toArray()))
        );
    }

    @BeforeEach
    public void setup() {
        apiEventRepository.deleteAll();
    }

    @AfterEach
    void reset() {
        githubWireMockServer.resetAll();
    }

    @Test
    @Order(0)
    public void index_repos_in_light_mode() {
        // Add repos to index in light mode
        repoIndexingJobEntityRepository.persist(new RepoIndexingJobEntity(BRETZEL_APP, null, false, true));

        // Run all jobs
        final var before = ZonedDateTime.now();
        diffRepoRefreshJobManager.createJob().run();
        final var after = ZonedDateTime.now();

        // Jobs are finished
        final var job = repoIndexingJobEntityRepository.findById(BRETZEL_APP).orElseThrow();
        assertThat(job.startedAt().atZone(ZoneOffset.UTC)).isNotNull().isAfter(before);
        assertThat(job.finishedAt().atZone(ZoneOffset.UTC)).isNotNull().isAfter(job.startedAt().atZone(ZoneOffset.UTC)).isBefore(after);
        assertThat(job.status()).isEqualTo(JobStatus.SUCCESS);

        final var stats = githubRepoStatsEntityRepository.findById(BRETZEL_APP).orElseThrow();
        assertThat(stats.getLastIndexedAt()).isNotNull();

        assertThat(apiEventRepository.findAll()).isEmpty();
    }

    @Test
    @Order(1)
    public void index_repos_in_full_mode() {
        // Add repos to index (full mode = from REST API)
        onRepoLinkChanged(List.of(BRETZEL_APP, MARKETPLACE), List.of()).expectStatus().isNoContent();

        // Jobs are pending, even if BRETZEL_APP was previously indexed in light mode
        assertThat(repoIndexingJobEntityRepository.findAll(Sort.by("repoId")))
                .usingRecursiveFieldByFieldElementComparator()
                .contains(
                        new RepoIndexingJobEntity(BRETZEL_APP, null, true, true),
                        new RepoIndexingJobEntity(MARKETPLACE, null, true, true)
                );

        // Run all jobs
        final var before = ZonedDateTime.now();
        diffRepoRefreshJobManager.createJob().run();
        final var after = ZonedDateTime.now();

        // Jobs are finished
        for (final var repoId : new Long[]{BRETZEL_APP, MARKETPLACE}) {
            final var job = repoIndexingJobEntityRepository.findById(repoId).orElseThrow();
            assertThat(job.startedAt().atZone(ZoneOffset.UTC)).isNotNull().isAfter(before);
            assertThat(job.finishedAt().atZone(ZoneOffset.UTC)).isNotNull().isAfter(job.startedAt().atZone(ZoneOffset.UTC)).isBefore(after);
            assertThat(job.status()).isEqualTo(JobStatus.SUCCESS);

            final var stats = githubRepoStatsEntityRepository.findById(repoId).orElseThrow();
            assertThat(stats.getLastIndexedAt()).isNotNull();
        }

        assertThat(apiEventRepository.findAll()).hasSize(9);
    }

    @Test
    @Order(2)
    public void should_expose_indexed_repo_even_if_no_contributions() {
        assertThat(githubRepoRepository.findById(BRETZEL_APP)).isPresent();
        assertThat(pullRequestsRepository.findAllByRepoId(BRETZEL_APP)).hasSize(0);
        assertThat(issuesRepository.findAllByRepoId(BRETZEL_APP)).hasSize(0);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getRepo().getId().equals(BRETZEL_APP))).hasSize(0);
        assertThat(repoContributorRepository.findAll().stream().filter(r -> r.getId().getRepoId().equals(BRETZEL_APP))).hasSize(0);

        assertThat(apiEventRepository.findAll()).isEmpty();
    }

    @Test
    @Order(2)
    @Transactional
    public void should_index_repo_with_contributions() {
        final var exposedRepo = githubRepoRepository.findById(MARKETPLACE);
        assertThat(exposedRepo).isPresent();
        assertThat(exposedRepo.get().getLanguages())
                .usingRecursiveFieldByFieldElementComparator()
                .containsExactlyInAnyOrder(
                        GithubRepoLanguageEntity.builder().repoId(MARKETPLACE).language("TypeScript").lineCount(2761826L).build(),
                        GithubRepoLanguageEntity.builder().repoId(MARKETPLACE).language("Shell").lineCount(11474L).build(),
                        GithubRepoLanguageEntity.builder().repoId(MARKETPLACE).language("CSS").lineCount(5535L).build(),
                        GithubRepoLanguageEntity.builder().repoId(MARKETPLACE).language("PLpgSQL").lineCount(1372L).build(),
                        GithubRepoLanguageEntity.builder().repoId(MARKETPLACE).language("JavaScript").lineCount(23763L).build(),
                        GithubRepoLanguageEntity.builder().repoId(MARKETPLACE).language("HTML").lineCount(1520L).build());
        assertThat(pullRequestsRepository.findAll()).hasSize(2);
        assertThat(issuesRepository.findAll()).hasSize(2);
        /*
         * Pull request 1257 from anthony (author is same as committer)
         * Code review from pierre
         * Code review requested to olivier
         * Pull request 1258 from anthony (author is same as committer)
         * Code review requested to olivier and pierre
         * Issue 78 assigned to anthony
         * Issue 82 not assigned
         */
        assertThat(contributionRepository.findAll()).hasSize(9);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.PULL_REQUEST)).hasSize(3);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.CODE_REVIEW)).hasSize(4);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.ISSUE)).hasSize(2);

        assertThat(groupedContributionRepository.findAll()).hasSize(8);
        assertThat(groupedContributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.PULL_REQUEST)).hasSize(2);
        assertThat(groupedContributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.CODE_REVIEW)).hasSize(4);
        assertThat(groupedContributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.ISSUE)).hasSize(2);

        assertThat(repoContributorRepository.findAll()).hasSize(3);
        assertThat(repoContributorRepository.findAll()).containsExactlyInAnyOrder(
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE, 43467246L), 2, 3),
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE, 16590657L), 1, 2),
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE, 595505L), 0, 3));
    }

    @Test
    @Order(3)
    @Transactional
    public void should_delete_contributions_before_saving_new_ones() {
        // Given
        // This will reset the jobs to force running them again
        onRepoLinkChanged(List.of(BRETZEL_APP, MARKETPLACE), List.of()).expectStatus().isNoContent();

        githubWireMockServer.stubFor(get(urlEqualTo("/repositories/498695724/pulls?state=all&sort=updated&per_page=100"))
                .atPriority(1)
                .willReturn(aResponse().withBodyFile("repos/marketplace-frontend/pulls-page-1-updated.json")));

        githubWireMockServer.stubFor(get(urlEqualTo("/repositories/498695724/pulls/1257"))
                .atPriority(1)
                .willReturn(aResponse().withBodyFile("repos/marketplace-frontend/pulls/1257-2.json")));

        githubWireMockServer.stubFor(get(urlEqualTo("/repositories/498695724/issues?state=all&sort=updated&per_page=100"))
                .atPriority(1)
                .willReturn(aResponse().withBodyFile("repos/marketplace-frontend/issues-page-1-updated.json")));

        githubWireMockServer.stubFor(get(urlEqualTo("/repositories/498695724/issues/78"))
                .atPriority(1)
                .willReturn(aResponse().withBodyFile("repos/marketplace-frontend/issues/78-2.json")));

        // When
        diffRepoRefreshJobManager.createJob().run();

        // Then
        /*
         * Pull request 1257 from anthony (author is same as committer)
         * Code review from pierre
         * Pull request 1258 from anthony (author is same as committer + olivier is committer)
         * Code review requested to olivier and pierre
         * Issue 78 assigned to olivier
         * Issue 82 not assigned
         */
        assertThat(contributionRepository.findAll()).hasSize(8);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.PULL_REQUEST)).hasSize(3);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.CODE_REVIEW)).hasSize(3);
        assertThat(groupedContributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.PULL_REQUEST)).hasSize(2);
        assertThat(groupedContributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.CODE_REVIEW)).hasSize(3);

        final var issues = contributionRepository.findAll().stream()
                .filter(c -> c.getType() == ContributionEntity.Type.ISSUE)
                .sorted(comparing(ContributionEntity::getGithubNumber))
                .toList();
        assertThat(issues).hasSize(2);
        assertThat(issues.get(0).getContributor().getLogin()).isEqualTo("ofux");
        assertThat(issues.get(1).getContributor()).isNull();
    }

    @Test
    @Order(4)
    public void should_change_to_light_mode_upon_request() {
        onRepoLinkChanged(List.of(), List.of(BRETZEL_APP)).expectStatus().isNoContent();
        assertThat(repoIndexingJobEntityRepository.findById(BRETZEL_APP).orElseThrow().fullIndexing()).isFalse();
    }

    @Test
    @Order(4)
    public void should_change_to_full_mode_upon_request() {
        onRepoLinkChanged(List.of(BRETZEL_APP), List.of()).expectStatus().isNoContent();
        assertThat(repoIndexingJobEntityRepository.findById(BRETZEL_APP).orElseThrow().fullIndexing()).isTrue();
    }
}
