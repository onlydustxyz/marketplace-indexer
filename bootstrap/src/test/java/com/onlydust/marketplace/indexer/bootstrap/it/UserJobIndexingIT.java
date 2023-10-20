package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserJobIndexingIT extends IntegrationTest {
    @Autowired
    public UserIndexingJobRepository userIndexingJobRepository;

    @Test
    public void should_index_user_on_demand() throws IOException {
        // Given
        final Long ANTHONY = 43467246L;

        // When
        final var response = indexUser(ANTHONY);

        // Then
        response.expectStatus().isNoContent();

        assertThat(userIndexingJobRepository.users()).containsExactly(ANTHONY);
    }

    private WebTestClient.ResponseSpec indexUser(Long userId) {
        return put("/api/v1/indexes/users/" + userId);
    }
}
