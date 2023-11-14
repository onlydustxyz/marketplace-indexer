package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobEntityRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

public class UserJobIndexingIT extends IntegrationTest {
    @Autowired
    public UserIndexingJobEntityRepository userIndexingJobEntityRepository;

    @Test
    public void should_index_user_on_demand() throws InterruptedException {
        // Given
        final Long ANTHONY = 43467246L;

        // When
        final var response = indexUser(ANTHONY);

        // Then
        response.expectStatus().isNoContent();

        waitForUserJobToFinish(ANTHONY);

        final var jobs = userIndexingJobEntityRepository.findAll();
        assertThat(jobs).hasSize(1);
        assertThat(jobs.get(0).getUserId()).isEqualTo(ANTHONY);
        assertThat(jobs.get(0).getStartedAt()).isNotNull();
        assertThat(jobs.get(0).getFinishedAt()).isNotNull();
        assertThat(jobs.get(0).getStatus()).isEqualTo(JobStatus.SUCCESS);
    }

    private WebTestClient.ResponseSpec indexUser(Long userId) {
        return put("/api/v1/indexes/users/" + userId);
    }
}
