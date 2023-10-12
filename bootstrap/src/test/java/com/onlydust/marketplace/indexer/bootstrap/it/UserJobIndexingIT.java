package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobTriggerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserJobIndexingIT extends IntegrationTest {
    @Autowired
    public UserIndexingJobTriggerRepository userIndexingJobTriggerRepository;

    @Test
    public void should_index_user_on_demand() throws IOException {
        // Given
        final Integer ANTHONY = 43467246;

        // When
        final var response = indexUser(ANTHONY);

        // Then
        response.expectStatus().isNoContent();

        assertThat(userIndexingJobTriggerRepository.list()).containsExactly(new UserIndexingJobTrigger(ANTHONY.longValue()));
    }

    private WebTestClient.ResponseSpec indexUser(Integer userId) {
        return put("/api/v1/indexes/users/" + userId);
    }
}
