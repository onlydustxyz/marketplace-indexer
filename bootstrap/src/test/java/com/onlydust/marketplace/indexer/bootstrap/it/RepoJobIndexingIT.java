package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobTriggerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class RepoJobIndexingIT extends IntegrationTest {
    @Autowired
    public RepoIndexingJobTriggerRepository repoIndexingJobTriggerRepository;

    @Test
    public void should_add_repo_to_index() throws IOException {
        // Given
        final Long MARKETPLACE = 498695724L;

        // When
        final var response = indexRepo(MARKETPLACE);

        // Then
        response.expectStatus().isNoContent();

        assertThat(repoIndexingJobTriggerRepository.list()).containsExactly(new RepoIndexingJobTrigger(0L, MARKETPLACE));
    }

    private WebTestClient.ResponseSpec indexRepo(Long repoId) {
        return put("/api/v1/indexes/repos/" + repoId);
    }
}
