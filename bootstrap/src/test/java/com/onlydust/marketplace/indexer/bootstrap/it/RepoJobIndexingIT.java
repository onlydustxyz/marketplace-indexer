package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
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
    public RepoIndexingJobEntityRepository repoIndexingJobEntityRepository;
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

        assertThat(repoIndexingJobEntityRepository.findAll()).contains(new RepoIndexingJobEntity(BRETZEL_APP, 0L));

        // Wait for the job to finish
        waitForJobToFinish(BRETZEL_APP);

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

        {
            final var job = repoIndexingJobEntityRepository.findById(MARKETPLACE);
            assertThat(job).isPresent();
            assertThat(job.get().getInstallationId()).isEqualTo(0L);
        }

        // Wait for the job to finish
        waitForJobToFinish(MARKETPLACE);

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

        {
            final var job = repoIndexingJobEntityRepository.findById(MARKETPLACE);
            assertThat(job).isPresent();
            assertThat(job.get().getStartedAt()).isNotNull();
            assertThat(job.get().getFinishedAt()).isNotNull();
            assertThat(job.get().getStatus()).isNotEqualTo(RepoIndexingJobEntity.Status.PENDING);
        }
    }

    private WebTestClient.ResponseSpec indexRepo(Long repoId) {
        return put("/api/v1/indexes/repos/" + repoId);
    }
}
