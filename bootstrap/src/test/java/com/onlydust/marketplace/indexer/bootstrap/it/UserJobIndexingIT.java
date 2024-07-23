package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.UserIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class UserJobIndexingIT extends IntegrationTest {
    private final static Long ANTHONY = 43467246L;

    @Autowired
    public UserIndexingJobEntityRepository userIndexingJobEntityRepository;
    @Autowired
    public JobManager diffUserRefreshJobManager;

    private WebTestClient.ResponseSpec indexUser(Long userId) {
        return put("/api/v1/indexes/users/" + userId);
    }

    @Test
    public void indexAllUsers() {
        // Add users to index
        indexUser(ANTHONY).expectStatus().isNoContent();

        // Jobs are pending
        assertThat(userIndexingJobEntityRepository.findAll()).containsExactly(
                new UserIndexingJobEntity(ANTHONY)
        );

        // Run all jobs
        diffUserRefreshJobManager.createJob().run();

        // Jobs are finished
        final var jobs = userIndexingJobEntityRepository.findAll();
        assertThat(jobs).hasSize(1);
        for (final var job : jobs) {
            assertThat(job.getStartedAt()).isNotNull();
            assertThat(job.getFinishedAt()).isNotNull();
            assertThat(job.getStatus()).isEqualTo(JobStatus.SUCCESS);
        }
    }
}
