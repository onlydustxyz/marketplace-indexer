package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.postgres.entities.*;
import com.onlydust.marketplace.indexer.postgres.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

public class PullRequestIndexingIT extends IntegrationTest {
    @Autowired
    public PullRequestRepository pullRequestsRepository;
    @Autowired
    public PullRequestCommitsRepository pullRequestsCommitsRepository;
    @Autowired
    public RepoCheckRunsRepository repoCheckRunsRepository;
    @Autowired
    public PullRequestReviewsRepository pullRequestReviewsRepository;
    @Autowired
    public RepoRepository repoRepository;
    @Autowired
    public UserRepository userRepository;
    @Autowired
    public UserSocialAccountsRepository userSocialAccountsRepository;

    @Test
    public void should_index_pull_request_on_demand() throws IOException {
        // Given
        final var marketplaceFrontend = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend.json"), RawRepo.class);
        final var pr1257 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257.json"), RawPullRequest.class);
        final var pr1257Reviews = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_reviews.json"), RawCodeReview[].class));
        final var pr1257Commits = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_commits.json"), RawCommit[].class));
        final var pr1257CheckRuns = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_check_runs.json"), RawCheckRuns.class);

        final var anthony = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony.json"), RawUser.class);
        final var pierre = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre.json"), RawUser.class);
        final var olivier = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/olivier.json"), RawUser.class);
        final var anthonySocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony_social_accounts.json"), RawSocialAccount[].class));
        final var pierreSocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre_social_accounts.json"), RawSocialAccount[].class));
        final var olivierSocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/olivier_social_accounts.json"), RawSocialAccount[].class));

        // When
        final var response = indexPullRequest("onlydustxyz", "marketplace-frontend", 1257);

        // Then
        response.expectStatus().isNoContent();

        assertThat(pullRequestsRepository.findAll()).containsExactly(PullRequest.of(marketplaceFrontend.getId(), pr1257));
        assertThat(repoRepository.findAll()).containsExactly(Repo.of(marketplaceFrontend));
        assertThat(pullRequestReviewsRepository.findAll()).containsExactly(PullRequestReview.of(pr1257.getId(), pr1257Reviews));
        assertThat(pullRequestsCommitsRepository.findAll()).containsExactly(PullRequestCommits.of(pr1257.getId(), pr1257Commits));
        assertThat(repoCheckRunsRepository.findAll()).containsExactly(RepoCheckRuns.of(marketplaceFrontend.getId(), pr1257.getHead().getSha(), pr1257CheckRuns));
        assertThat(userRepository.findAll()).containsExactly(User.of(pierre), User.of(olivier), User.of(anthony));
        assertThat(userSocialAccountsRepository.findAll()).containsExactly(
                UserSocialAccounts.of(pierre.getId(), pierreSocialAccounts),
                UserSocialAccounts.of(olivier.getId(), olivierSocialAccounts),
                UserSocialAccounts.of(anthony.getId(), anthonySocialAccounts)
        );
    }

    private WebTestClient.ResponseSpec indexPullRequest(String repoOwner, String repoName, Integer pullRequestNumber) {
        return put("/api/v1/repos/" + repoOwner + "/" + repoName + "/pull-requests/" + pullRequestNumber);
    }
}