package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobTriggerRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.IssueRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.PullRequestRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.RepoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class RepoJobIndexingIT extends IntegrationTest {
    @Autowired
    public RepoIndexingJobTriggerRepository repoIndexingJobTriggerRepository;
    @Autowired
    public RepoRepository repoRepository;
    @Autowired
    public PullRequestRepository pullRequestsRepository;
    @Autowired
    public IssueRepository issuesRepository;

    @Test
    public void should_add_repo_to_index() throws IOException, InterruptedException {
        // Given
        final Long MARKETPLACE = 498695724L;

        // When
        final var response = indexRepo(MARKETPLACE);

        // Then
        response.expectStatus().isNoContent();

        assertThat(repoIndexingJobTriggerRepository.list()).containsExactly(new RepoIndexingJobTrigger(0L, MARKETPLACE));

        // Wait for the job to finish
        waitForJobToFinish();

        assertThat(repoRepository.findAll()).hasSize(1);
        assertThat(pullRequestsRepository.findAll()).hasSize(2);
        assertThat(issuesRepository.findAll()).hasSize(2);
    }

    private void waitForJobToFinish() throws InterruptedException {
        for (int i = 0; i < 10 && isJobRunning(); i++) {
            Thread.sleep(1000);
        }
    }

    private boolean isJobRunning() {
        return repoRepository.findAll().isEmpty() ||
                pullRequestsRepository.findAll().isEmpty() ||
                issuesRepository.findAll().isEmpty();
    }

    private WebTestClient.ResponseSpec indexRepo(Long repoId) {
        return put("/api/v1/indexes/repos/" + repoId);
    }
}
