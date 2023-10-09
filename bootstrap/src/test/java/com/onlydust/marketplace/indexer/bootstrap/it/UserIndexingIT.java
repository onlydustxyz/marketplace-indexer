package com.onlydust.marketplace.indexer.bootstrap.it;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.RawUser;
import com.onlydust.marketplace.indexer.postgres.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class UserIndexingIT extends IntegrationTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public UserRepository userRepository;

    @BeforeEach
    void setup() {
        mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
    }

    @Test
    public void should_index_user_on_demand() throws IOException {
        // Given
        final Integer ANTHONY = 43467246;

        // When
        final var response = indexUser(ANTHONY);

        // Then
        response.expectStatus().isNoContent();

        final var expectedUser = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony.json"), RawUser.class);

        final var users = userRepository.findAll();
        assertThat(users.size()).isEqualTo(1);
        assertThat(users.get(0).getId()).isEqualTo(expectedUser.getId());
        assertThat(users.get(0).getLogin()).isEqualTo(expectedUser.getLogin());
        assertThat(users.get(0).getData()).isEqualTo(expectedUser);
        assertThat(users.get(0).getCreatedAt()).isNotNull();
        assertThat(users.get(0).getUpdatedAt()).isNotNull();
    }

    private WebTestClient.ResponseSpec indexUser(Integer userId) {
        return put("/api/v1/users/" + userId);
    }
}
