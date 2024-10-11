package com.onlydust.marketplace.indexer.bootstrap.it;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import com.onlydust.marketplace.indexer.postgres.entities.raw.RawUserEntity;
import com.onlydust.marketplace.indexer.postgres.entities.raw.RawUserSocialAccountsEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.UserRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.UserSocialAccountsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;
import static org.assertj.core.api.Assertions.assertThat;

public class UserIndexingIT extends IntegrationTest {
    private final ObjectMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public UserSocialAccountsRepository userSocialAccountsRepository;
    @Autowired
    public GithubAccountEntityRepository githubAccountEntityRepository;
    @Autowired
    public UserIndexingJobEntityRepository userIndexingJobEntityRepository;

    @BeforeEach
    void setup() {
        userSocialAccountsRepository.deleteAll();
        userRepository.deleteAll();
        githubWireMockServer.resetAll();
        mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
    }

    @Test
    public void should_add_user_to_index() throws IOException {
        // Given
        final Long ANTHONY = 43467246L;

        // When
        final var response = indexUser(ANTHONY);

        // Then
        response.expectStatus().isNoContent();

        final var expectedUser = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony.json"), RawAccount.class);
        assertThat(userRepository.findAll()).usingFieldByFieldElementComparator().containsExactly(RawUserEntity.of(expectedUser));

        final var expectedUserSocialAccounts = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony_social_accounts.json")
                , RawSocialAccount[].class);
        assertThat(userSocialAccountsRepository.findAll())
                .usingFieldByFieldElementComparator()
                .containsExactly(RawUserSocialAccountsEntity.of(ANTHONY, Arrays.asList(expectedUserSocialAccounts)));

        final var user = githubAccountEntityRepository.findById(ANTHONY);
        assertThat(user).isPresent();
        assertThat(user.get().getId()).isEqualTo(ANTHONY);
        assertThat(user.get().getLogin()).isEqualTo("AnthonyBuisset");
        assertThat(user.get().getType()).isEqualTo(GithubAccountEntity.Type.USER);
        assertThat(user.get().getCreatedAt()).isEqualToIgnoringNanos(ZonedDateTime.parse("2018-09-21T08:45:50Z"));

        assertThat(userIndexingJobEntityRepository.findById(ANTHONY)).isPresent();
    }

    @ParameterizedTest
    @EnumSource(value = HttpStatus.class, names = {"INTERNAL_SERVER_ERROR", "BAD_GATEWAY", "SERVICE_UNAVAILABLE", "GATEWAY_TIMEOUT"})
    public void should_retry_upon_some_failures(HttpStatus errorStatus) {
        // Given
        final var ANTHONY = 43467246L;

        final BiFunction<String, String, StubMapping> failureStub = (fromState, toState) -> githubWireMockServer.stubFor(get(urlEqualTo("/user/" + ANTHONY))
                .inScenario("Retry")
                .whenScenarioStateIs(fromState)
                .willSetStateTo(toState)
                .willReturn(aResponse().withStatus(errorStatus.value()).withBody("Internal server error")));

        failureStub.apply(STARTED, "First failure");
        failureStub.apply("First failure", "Second failure");
        failureStub.apply("Second failure", "Third failure");

        // When
        indexUser(ANTHONY)
                // Then
                .expectStatus()
                .isNoContent();

        githubWireMockServer.verify(4, getRequestedFor(urlEqualTo("/user/" + ANTHONY)));
    }

    private WebTestClient.ResponseSpec indexUser(Long userId) {
        return put("/api/v1/users/" + userId, Map.of("Authorization", "Bearer ghp_GITHUB_USER_PAT"));
    }
}
