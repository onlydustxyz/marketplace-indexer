package com.onlydust.marketplace.indexer.bootstrap.it;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawUser;
import com.onlydust.marketplace.indexer.postgres.entities.User;
import com.onlydust.marketplace.indexer.postgres.entities.UserSocialAccounts;
import com.onlydust.marketplace.indexer.postgres.repositories.UserRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.UserSocialAccountsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class UserIndexingIT extends IntegrationTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public UserSocialAccountsRepository userSocialAccountsRepository;

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
        assertThat(userRepository.findAll()).containsExactly(User.of(expectedUser));

        final var expectedUserSocialAccounts = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony-social_accounts.json"), RawSocialAccount[].class);
        assertThat(userSocialAccountsRepository.findAll()).containsExactly(UserSocialAccounts.of(ANTHONY.longValue(), Arrays.asList(expectedUserSocialAccounts)));
    }

    private WebTestClient.ResponseSpec indexUser(Integer userId) {
        return put("/api/v1/users/" + userId);
    }
}
