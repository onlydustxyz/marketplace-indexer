package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.entities.raw.*;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubPullRequestRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.*;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
    @Autowired
    public ContributionRepository contributionRepository;
    @Autowired
    public GithubPullRequestRepository githubPullRequestRepository;

    @Test
    @Order(1)
    public void should_index_pull_request_on_demand() throws IOException {
        // Given
        final var marketplaceFrontend = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend.json"), RawRepo.class);
        final var pr1257 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257.json"), RawPullRequest.class);
        final var pr1257Reviews = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_reviews.json"), RawCodeReview[].class));
        final var pr1257Commits = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_commits.json"), RawCommit[].class));
        final var pr1257CheckRuns = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_check_runs.json"), RawCheckRuns.class);
        final var pr1257ClosingIssues = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257_closing_issues.json"), RawPullRequestClosingIssues.class);
        final var issue78 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/issues/78.json"), RawIssue.class);

        final var anthony = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony.json"), RawAccount.class);
        final var pierre = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre.json"), RawAccount.class);
        final var olivier = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/olivier.json"), RawAccount.class);
        final var onlyDust = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/onlyDust.json"), RawAccount.class);
        final var anthonySocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony_social_accounts.json"), RawSocialAccount[].class));
        final var pierreSocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre_social_accounts.json"), RawSocialAccount[].class));
        final var olivierSocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/olivier_social_accounts.json"), RawSocialAccount[].class));

        // When
        final var response = indexPullRequest("onlydustxyz", "marketplace-frontend", 1257L, "ghp_GITHUB_USER_PAT");

        // Then
        response.expectStatus().isNoContent();

        assertThat(pullRequestsRepository.findAll()).containsExactly(PullRequest.of(marketplaceFrontend.getId(), pr1257));
        assertThat(repoRepository.findAll()).containsExactly(Repo.of(marketplaceFrontend));
        assertThat(pullRequestReviewsRepository.findAll()).containsExactly(PullRequestReview.of(pr1257.getId(), pr1257Reviews));
        assertThat(pullRequestsCommitsRepository.findAll()).containsExactly(PullRequestCommits.of(pr1257.getId(), pr1257Commits));
        assertThat(repoCheckRunsRepository.findAll()).containsExactly(RepoCheckRuns.of(marketplaceFrontend.getId(), pr1257.getHead().getSha(), pr1257CheckRuns));
        assertThat(userRepository.findAll()).containsExactlyInAnyOrder(User.of(pierre), User.of(olivier), User.of(anthony), User.of(onlyDust));
        assertThat(userSocialAccountsRepository.findAll()).containsExactlyInAnyOrder(
                UserSocialAccounts.of(pierre.getId(), pierreSocialAccounts),
                UserSocialAccounts.of(olivier.getId(), olivierSocialAccounts),
                UserSocialAccounts.of(anthony.getId(), anthonySocialAccounts),
                UserSocialAccounts.of(onlyDust.getId(), List.of())
        );
        assertThat(issueRepository.findAll()).containsExactly(Issue.of(marketplaceFrontend.getId(), issue78));
        assertThat(pullRequestClosingIssueRepository.findAll()).containsExactly(
                PullRequestClosingIssues.of(marketplaceFrontend.getOwner().getLogin(), marketplaceFrontend.getName(), pr1257.getNumber(), pr1257ClosingIssues));
        /*
         * Pull request 1257 from anthony (author is same as committer)
         * Code review from pierre
         * Code review requested to olivier
         * Issue 78 closed by pr 1257, assigned to anthony
         */
        assertThat(contributionRepository.findAll()).hasSize(4);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.PULL_REQUEST)).hasSize(1);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.CODE_REVIEW)).hasSize(2);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.ISSUE)).hasSize(1);

        assertThat(githubPullRequestRepository.findAll()).hasSize(1);
        final var githubPullRequest = githubPullRequestRepository.findAll().stream().findFirst().orElseThrow();
        assertThat(githubPullRequest.getCreatedAt().toString()).isEqualTo("2023-09-21T12:42:45Z");
        assertThat(githubPullRequest.getCommitCount()).isEqualTo(1);
        final var commitCounts = githubPullRequest.getCommitCounts().stream().findFirst().orElseThrow();
        assertThat(commitCounts.getPullRequestId()).isEqualTo(pr1257.getId());
        assertThat(commitCounts.getAuthor().getId()).isEqualTo(anthony.getId());
        assertThat(commitCounts.getCommitCount()).isEqualTo(1);
    }

    @Test
    @Order(2)
    public void should_index_pull_request_with_multiple_commits_on_demand() throws IOException {
        // Given
        final var marketplaceFrontend = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend.json"), RawRepo.class);
        final var pr1258 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1258.json"), RawPullRequest.class);
        final var pr1258Reviews = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1258_reviews.json"), RawCodeReview[].class));
        final var pr1258Commits = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1258_commits.json"), RawCommit[].class));
        final var pr1258CheckRuns = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1258_check_runs.json"), RawCheckRuns.class);

        final var anthony = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony.json"), RawAccount.class);
        final var pierre = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre.json"), RawAccount.class);
        final var olivier = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/olivier.json"), RawAccount.class);
        final var onlyDust = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/onlyDust.json"), RawAccount.class);

        // When
        final var response = indexPullRequest("onlydustxyz", "marketplace-frontend", 1258L, "GITHUB_PAT");

        // Then
        response.expectStatus().isNoContent();

        assertThat(pullRequestsRepository.findAll()).contains(PullRequest.of(marketplaceFrontend.getId(), pr1258));
        assertThat(pullRequestReviewsRepository.findAll()).contains(PullRequestReview.of(pr1258.getId(), pr1258Reviews));
        assertThat(pullRequestsCommitsRepository.findAll()).contains(PullRequestCommits.of(pr1258.getId(), pr1258Commits));
        assertThat(repoCheckRunsRepository.findAll()).contains(RepoCheckRuns.of(marketplaceFrontend.getId(), pr1258.getHead().getSha(), pr1258CheckRuns));
        assertThat(userRepository.findAll()).contains(User.of(pierre), User.of(olivier), User.of(anthony), User.of(onlyDust));

        assertThat(contributionRepository.findAll().size()).isEqualTo(7);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.PULL_REQUEST).toList().size()).isEqualTo(2);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.CODE_REVIEW).toList().size()).isEqualTo(4);
        assertThat(contributionRepository.findAll().stream().filter(c -> c.getType() == ContributionEntity.Type.ISSUE).toList().size()).isEqualTo(1);

        assertThat(githubPullRequestRepository.findAll()).hasSize(2);
        final var githubPullRequest = githubPullRequestRepository.findAll().stream().filter(pr -> pr.getNumber() == 1258L).findFirst().orElseThrow();
        assertThat(githubPullRequest.getCommitCount()).isEqualTo(12);
        final var commitCounts = githubPullRequest.getCommitCounts().stream().findFirst().orElseThrow();
        assertThat(commitCounts.getPullRequestId()).isEqualTo(pr1258.getId());
        assertThat(commitCounts.getAuthor().getId()).isEqualTo(anthony.getId());
        assertThat(commitCounts.getCommitCount()).isEqualTo(12);
    }

    private WebTestClient.ResponseSpec indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber, String token) {
        return put("/api/v1/repos/" + repoOwner + "/" + repoName + "/pull-requests/" + pullRequestNumber, Map.of("Authorization", "Bearer " + token));
    }
}
