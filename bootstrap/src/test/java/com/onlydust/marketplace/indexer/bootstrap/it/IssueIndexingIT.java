package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawUser;
import com.onlydust.marketplace.indexer.postgres.entities.Issue;
import com.onlydust.marketplace.indexer.postgres.entities.Repo;
import com.onlydust.marketplace.indexer.postgres.entities.User;
import com.onlydust.marketplace.indexer.postgres.entities.UserSocialAccounts;
import com.onlydust.marketplace.indexer.postgres.repositories.IssueRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.UserRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.UserSocialAccountsRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class IssueIndexingIT extends IntegrationTest {
    @Autowired
    public IssueRepository issueRepository;
    @Autowired
    public RepoRepository repoRepository;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public UserSocialAccountsRepository userSocialAccountsRepository;

    @Test
    public void should_index_issue_on_demand() throws IOException {
        // Given
        final var marketplaceFrontend = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend.json"), RawRepo.class);
        final var issue78 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/issues/78.json"), RawIssue.class);
        final var anthony = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony.json"), RawUser.class);
        final var anthonySocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony_social_accounts.json"), RawSocialAccount[].class));

        // When
        final var response = indexIssue("onlydustxyz", "marketplace-frontend", 78);

        // Then
        response.expectStatus().isNoContent();

        assertThat(issueRepository.findAll()).containsExactly(Issue.of(marketplaceFrontend.getId(), issue78));
        assertThat(repoRepository.findAll()).containsExactly(Repo.of(marketplaceFrontend));
        assertThat(userRepository.findAll()).containsExactly(User.of(anthony));
        assertThat(userSocialAccountsRepository.findAll()).containsExactly(UserSocialAccounts.of(anthony.getId(), anthonySocialAccounts));
    }

    private WebTestClient.ResponseSpec indexIssue(String repoOwner, String repoName, Integer issueNumber) {
        return put("/api/v1/repos/" + repoOwner + "/" + repoName + "/issues/" + issueNumber);
    }
}