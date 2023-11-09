package com.onlydust.marketplace.indexer.bootstrap.it;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.WebTestClient;

public class AuthenticationIT extends IntegrationTest {
    @Test
    public void should_unauthorize_if_api_key_is_missing() {
        // When
        final var response = sendRequest("/api/v1/users/0");

        // Then
        response.expectStatus().isUnauthorized();
    }

    private WebTestClient.ResponseSpec sendRequest(String path) {
        return client.put().uri(getApiURI(path)).exchange();
    }
}
