package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.jobs.EventsInboxJob;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoStatsEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.IssueRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.PullRequestRepository;
import com.onlydust.marketplace.indexer.rest.github.GithubWebhookRestApi;
import com.onlydust.marketplace.indexer.rest.github.security.GithubSignatureVerifier;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LightRepoJobIndexingIT extends IntegrationTest {
    final static Long MARKETPLACE = 498695724L;
    final static Long CAIRO_STREAMS = 493795808L;

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
    public GithubRepoStatsEntityRepository githubRepoStatsEntityRepository;
    @Autowired
    public JobManager diffRepoRefreshJobManager;
    @Autowired
    GithubWebhookRestApi.Config config;
    @Autowired
    EventsInboxJob eventsInboxJob;

    @Test
    @Order(1)
    public void index_repos() {
        // Add repos to index (light mode = from GitHub app installation)
        processEventsFromPaths("installation",
                "/github/webhook/events/installation/installation_created_new.json", // Add repos to index (light mode = from GitHub app installation)
                "/github/webhook/events/installation/installation_added_private_repo.json", // Add private repo
                "/github/webhook/events/installation/installation_deleted.json"             // Uninstall app
        );

        // Run all jobs
        diffRepoRefreshJobManager.createJob().run();

        // Jobs are finished
        for (final var repoId : new Long[]{MARKETPLACE}) {
            final var job = repoIndexingJobEntityRepository.findById(repoId).orElseThrow();
            assertThat(job.getStartedAt()).isNotNull();
            assertThat(job.getFinishedAt()).isNotNull();
            assertThat(job.getStatus()).isEqualTo(JobStatus.SUCCESS);

            final var stats = githubRepoStatsEntityRepository.findById(repoId).orElseThrow();
            assertThat(stats.getLastIndexedAt()).isNotNull();
        }

        // Some jobs did not run
        for (final var repoId : new Long[]{CAIRO_STREAMS}) {
            final var job = repoIndexingJobEntityRepository.findById(repoId).orElseThrow();
            assertThat(job.getStartedAt()).isNull();
            assertThat(job.getFinishedAt()).isNull();
            assertThat(job.getStatus()).isEqualTo(JobStatus.PENDING);

            assertThat(githubRepoStatsEntityRepository.findById(repoId)).isEmpty();
        }
    }

    @Test
    @Order(2)
    public void should_expose_indexed_repo_but_no_contribution() {
        final var repo = githubRepoEntityRepository.findById(MARKETPLACE).orElseThrow();
        assertThat(repo.getDescription()).isEqualTo("Contributions marketplace backend services");
        assertThat(pullRequestsRepository.findAll()).isEmpty();
        assertThat(issuesRepository.findAll()).isEmpty();
        assertThat(contributionRepository.findAll()).isEmpty();
        assertThat(repoContributorRepository.findAll()).isEmpty();
    }

    @Test
    @Order(3)
    public void should_not_index_private_repos() {
        final var repo = githubRepoEntityRepository.findById(CAIRO_STREAMS).orElseThrow();
        assertThat(repo.getDescription()).isNull();
        assertThat(pullRequestsRepository.findAll()).isEmpty();
        assertThat(issuesRepository.findAll()).isEmpty();
        assertThat(contributionRepository.findAll()).isEmpty();
        assertThat(repoContributorRepository.findAll()).isEmpty();
    }

    protected WebTestClient.ResponseSpec post(final String event, String eventTypeHeader) {
        return client.post().uri(getApiURI("/github-app/webhook")).header("X-GitHub-Event", eventTypeHeader)
                .header("X-Hub-Signature-256", "sha256=" + GithubSignatureVerifier.hmac(event.getBytes(),
                        config.secret))
                .bodyValue(event)
                .exchange();
    }
}
