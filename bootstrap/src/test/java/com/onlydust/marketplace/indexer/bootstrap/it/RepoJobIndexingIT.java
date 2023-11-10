package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTriggerEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobTriggerEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class RepoJobIndexingIT extends IntegrationTest {
    @Autowired
    public RepoIndexingJobTriggerEntityRepository repoIndexingJobTriggerRepository;
    @Autowired
    public ContributionRepository contributionRepository;
    @Autowired
    public RepoContributorRepository repoContributorRepository;


    @Test
    @Order(1)
    public void should_add_repo_to_index_even_if_no_contributions() throws InterruptedException {
        // Given
        final Long BRETZEL_APP = 380954304L;

        // When
        final var response = indexRepo(BRETZEL_APP);

        // Then
        response.expectStatus().isNoContent();

        assertThat(repoIndexingJobTriggerRepository.findAll()).contains(new RepoIndexingJobTriggerEntity(BRETZEL_APP, 0L));

        // Wait for the job to finish
        waitForJobToFinish(BRETZEL_APP, 0, 0);

        assertThat(githubRepoEntityRepository.findById(BRETZEL_APP)).isPresent();
        assertThat(pullRequestsRepository.findAll()).hasSize(0);
        assertThat(issuesRepository.findAll()).hasSize(0);
        assertThat(contributionRepository.findAll()).hasSize(0);
        assertThat(repoContributorRepository.findAll()).hasSize(0);
    }

    @Test
    @Order(2)
    public void should_add_repo_to_index() throws InterruptedException {
        // Given
        final Long MARKETPLACE = 498695724L;

        // When
        final var response = indexRepo(MARKETPLACE);

        // Then
        response.expectStatus().isNoContent();

        assertThat(repoIndexingJobTriggerRepository.findAll()).contains(new RepoIndexingJobTriggerEntity(MARKETPLACE, 0L));

        // Wait for the job to finish
        waitForJobToFinish(MARKETPLACE, 2, 2);

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
    }

    private WebTestClient.ResponseSpec indexRepo(Long repoId) {
        return put("/api/v1/indexes/repos/" + repoId);
    }
}
