package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.postgres.entities.raw.*;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

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
    @Autowired
    public IssueRepository issueRepository;
    @Autowired
    public PullRequestClosingIssueRepository pullRequestClosingIssueRepository;

    @Test
    public void should_index_pull_request_on_demand() throws IOException {
        // Given
        final var marketplaceFrontend = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend.json"), RawRepo.class);
        final var pr1257 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257.json"), RawPullRequest.class);
        final var pr1257Reviews = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_reviews.json"), RawCodeReview[].class));
        final var pr1257Commits = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_commits.json"), RawCommit[].class));
        final var pr1257CheckRuns = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_check_runs.json"), RawCheckRuns.class);
        final var issue82 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/issues/82.json"), RawIssue.class);
        final var pr1257ClosingIssues = new RawPullRequestClosingIssues(pr1257.getId(), List.of(Pair.of(issue82.getId(), issue82.getNumber())));

        final var anthony = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony.json"), RawAccount.class);
        final var pierre = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre.json"), RawAccount.class);
        final var olivier = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/olivier.json"), RawAccount.class);
        final var anthonySocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony_social_accounts.json"), RawSocialAccount[].class));
        final var pierreSocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre_social_accounts.json"), RawSocialAccount[].class));
        final var olivierSocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/olivier_social_accounts.json"), RawSocialAccount[].class));

        // When
        final var response = indexPullRequest("onlydustxyz", "marketplace-frontend", 1257L);

        // Then
        response.expectStatus().isNoContent();

        assertThat(pullRequestsRepository.findAll()).containsExactly(PullRequest.of(marketplaceFrontend.getId(), pr1257));
        assertThat(repoRepository.findAll()).containsExactly(Repo.of(marketplaceFrontend));
        assertThat(pullRequestReviewsRepository.findAll()).containsExactly(PullRequestReview.of(pr1257.getId(), pr1257Reviews));
        assertThat(pullRequestsCommitsRepository.findAll()).containsExactly(PullRequestCommits.of(pr1257.getId(), pr1257Commits));
        assertThat(repoCheckRunsRepository.findAll()).containsExactly(RepoCheckRuns.of(marketplaceFrontend.getId(), pr1257.getHead().getSha(), pr1257CheckRuns));
        assertThat(userRepository.findAll()).containsExactlyInAnyOrder(User.of(pierre), User.of(olivier), User.of(anthony));
        assertThat(userSocialAccountsRepository.findAll()).containsExactlyInAnyOrder(
                UserSocialAccounts.of(pierre.getId(), pierreSocialAccounts),
                UserSocialAccounts.of(olivier.getId(), olivierSocialAccounts),
                UserSocialAccounts.of(anthony.getId(), anthonySocialAccounts)
        );
        assertThat(issueRepository.findAll()).containsExactly(Issue.of(marketplaceFrontend.getId(), issue82));
        assertThat(pullRequestClosingIssueRepository.findAll()).containsExactly(PullRequestClosingIssue.of(pr1257.getId(), issue82.getId()));
    }

    private WebTestClient.ResponseSpec indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber) {
        return put("/api/v1/repos/" + repoOwner + "/" + repoName + "/pull-requests/" + pullRequestNumber);
    }
}
