package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.RepoContributorEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.IssueRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.PullRequestRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FullRepoJobIndexingIT extends IntegrationTest {
    final static Long MARKETPLACE = 498695724L;
    final static Long BRETZEL_APP = 380954304L;

    @Autowired
    public ContributionRepository contributionRepository;
    @Autowired
    public RepoContributorRepository repoContributorRepository;
    @Autowired
    public PullRequestRepository pullRequestsRepository;
    @Autowired
    public IssueRepository issuesRepository;
    @Autowired
    public GithubRepoEntityRepository githubRepoEntityRepository;
    @Autowired
    public RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
    @Autowired
    public JobManager diffRepoRefreshJobManager;
    @Autowired
    public JobManager apiNotifier;

    private WebTestClient.ResponseSpec onRepoLinkChanged(List<Long> linkedRepoIds, List<Long> unlinkedRepoIds) {
        return post("/api/v1/events/on-repo-link-changed", """
                {
                    "linkedRepoIds": %s,
                    "unlinkedRepoIds": %s
                }
                """.formatted(Arrays.toString(linkedRepoIds.toArray()), Arrays.toString(unlinkedRepoIds.toArray()))
        );
    }

    @Test
    @Order(1)
    public void index_repos() {
        // Add repos to index (full mode = from REST API)
        onRepoLinkChanged(List.of(BRETZEL_APP, MARKETPLACE), List.of()).expectStatus().isNoContent();

        // Jobs are pending
        assertThat(repoIndexingJobEntityRepository.findAll(Sort.by("repoId"))).contains(
                new RepoIndexingJobEntity(BRETZEL_APP, null, true, true),
                new RepoIndexingJobEntity(MARKETPLACE, null, true, true)
        );

        // Run all jobs
        diffRepoRefreshJobManager.allJobs().forEach(Job::run);

        // Jobs are finished
        for (final var repoId : new Long[]{BRETZEL_APP, MARKETPLACE}) {
            final var job = repoIndexingJobEntityRepository.findById(repoId).orElseThrow();
            assertThat(job.getStartedAt()).isNotNull();
            assertThat(job.getFinishedAt()).isNotNull();
            assertThat(job.getStatus()).isEqualTo(JobStatus.SUCCESS);
        }
    }

    @Test
    @Order(2)
    public void should_expose_indexed_repo_even_if_no_contributions() {
        assertThat(githubRepoEntityRepository.findById(BRETZEL_APP)).isPresent();
        assertThat(pullRequestsRepository.findAllByRepoId(BRETZEL_APP)).hasSize(0);
        assertThat(issuesRepository.findAllByRepoId(BRETZEL_APP)).hasSize(0);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getRepo().getId().equals(BRETZEL_APP))).hasSize(0);
        assertThat(repoContributorRepository.findAll().stream().filter(r -> r.getId().getRepoId().equals(BRETZEL_APP))).hasSize(0);
    }

    @Test
    @Order(2)
    public void should_index_repo_with_contributions() {
        assertThat(githubRepoEntityRepository.findById(MARKETPLACE)).isPresent();
        assertThat(pullRequestsRepository.findAll()).hasSize(2);
        assertThat(issuesRepository.findAll()).hasSize(2);
        /*
         * Pull request 1257 from anthony (author is same as committer)
         * Code review from pierre
         * Code review requested to olivier
         * Pull request 1258 from anthony (author is same as committer)
         * Code review requested to olivier and pierre
         * Issue 78 assigned to anthony
         */
        assertThat(contributionRepository.findAll()).hasSize(7);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.PULL_REQUEST)).hasSize(2);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.CODE_REVIEW)).hasSize(4);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.ISSUE)).hasSize(1);
        assertThat(repoContributorRepository.findAll()).hasSize(3);

        assertThat(repoContributorRepository.findAll()).containsExactlyInAnyOrder(
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE, 43467246L), 2, 3),
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE, 16590657L), 1, 2),
                new RepoContributorEntity(new RepoContributorEntity.Id(MARKETPLACE, 595505L), 0, 2));
    }

    @Test
    @Order(3)
    public void should_delete_contributions_before_saving_new_ones() {
        // Given
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
        diffRepoRefreshJobManager.allJobs().forEach(Job::run);

        // Then
        /*
         * Pull request 1257 from anthony (author is same as committer)
         * Code review from pierre
         * Pull request 1258 from anthony (author is same as committer)
         * Code review requested to olivier and pierre
         * Issue 78 assigned to olivier
         */
        assertThat(contributionRepository.findAll()).hasSize(6);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.PULL_REQUEST)).hasSize(2);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.CODE_REVIEW)).hasSize(3);

        final var issues = contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.ISSUE).toList();
        assertThat(issues).hasSize(1);
        assertThat(issues.get(0).getContributor().getLogin()).isEqualTo("ofux");
    }

    @Test
    @Order(4)
    public void should_change_to_light_mode_upon_request() {
        onRepoLinkChanged(List.of(), List.of(BRETZEL_APP)).expectStatus().isNoContent();
        assertThat(repoIndexingJobEntityRepository.findById(BRETZEL_APP).orElseThrow().getFullIndexing()).isFalse();
    }

    @Test
    @Order(4)
    public void should_change_to_full_mode_upon_request() {
        onRepoLinkChanged(List.of(BRETZEL_APP), List.of()).expectStatus().isNoContent();
        assertThat(repoIndexingJobEntityRepository.findById(BRETZEL_APP).orElseThrow().getFullIndexing()).isTrue();
    }

    @Test
    @Order(100)
    public void should_notify_api_upon_new_contributions() throws InterruptedException {
        apiWireMockServer.stubFor(com.github.tomakehurst.wiremock.client.WireMock.post(urlEqualTo("/api/v1/events/on-contributions-change"))
                .withHeader("Api-Key", equalTo("INTERNAL_API_KEY"))
                .withRequestBody(equalToJson("{\"repoIds\": [%d]}".formatted(MARKETPLACE)))
                .willReturn(noContent()));

        apiNotifier.allJobs().forEach(Job::run);
        Thread.sleep(10);
        apiNotifier.allJobs().forEach(Job::run); // This run will not send any event

        assertThat(apiWireMockServer
                .countRequestsMatching(postRequestedFor(urlEqualTo("/api/v1/events/on-contributions-change")).build())
                .getCount()
        ).isEqualTo(1);
    }
}
