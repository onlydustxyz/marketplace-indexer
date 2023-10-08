package com.onlydust.marketplace.indexer.bootstrap.it;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

public class UserIndexingIT extends IntegrationTest {

    @Test
    public void should_index_user_on_demand() {
        // Given
        final Integer ANTHONY = 43467246;

        // When
        final var response = indexUser(ANTHONY);

        // Then
        response.expectStatus().isNoContent();
    }

    private WebTestClient.ResponseSpec indexUser(Integer userId) {
        return put("/api/v1/users/" + userId);
    }
}
