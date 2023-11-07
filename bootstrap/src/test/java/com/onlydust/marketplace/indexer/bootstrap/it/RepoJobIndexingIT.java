package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTriggerEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobTriggerEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class RepoJobIndexingIT extends IntegrationTest {
    @Autowired
    public RepoIndexingJobTriggerEntityRepository repoIndexingJobTriggerRepository;
    @Autowired
    public ContributionRepository contributionRepository;

    @Test
    public void should_add_repo_to_index() throws IOException, InterruptedException {
        // Given
        final Long MARKETPLACE = 498695724L;

        // When
        final var response = indexRepo(MARKETPLACE);

        // Then
        response.expectStatus().isNoContent();

        assertThat(repoIndexingJobTriggerRepository.findAll()).containsExactly(new RepoIndexingJobTriggerEntity(MARKETPLACE, 0L));

        // Wait for the job to finish
        waitForJobToFinish(1, 2, 2);

        assertThat(repoRepository.findAll()).hasSize(1);
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
    }

    private WebTestClient.ResponseSpec indexRepo(Long repoId) {
        return put("/api/v1/indexes/repos/" + repoId);
    }
}
