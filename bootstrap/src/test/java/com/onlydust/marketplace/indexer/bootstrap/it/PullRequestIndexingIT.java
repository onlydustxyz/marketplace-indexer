package com.onlydust.marketplace.indexer.bootstrap.it;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.entities.raw.*;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubPullRequestRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.*;
import lombok.SneakyThrows;
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
        final var marketplaceFrontend = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend.json"),
                RawRepo.class);
        final var pr1257 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1257.json"),
                RawPullRequest.class);
        final var pr1257Reviews = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls" +
                                                                                                "/1257_reviews.json"), RawCodeReview[].class));
        final var pr1257Commits = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls" +
                                                                                                "/1257_commits.json"), RawCommit[].class));
        final var pr1257ClosingIssues = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls" +
                                                                                        "/1257_closing_issues.json"), RawPullRequestClosingIssues.class);
        final var issue78 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/issues/78.json"),
                RawIssue.class);

        final var anthony = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony.json"), RawAccount.class);
        final var pierre = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre.json"), RawAccount.class);
        final var olivier = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/olivier.json"), RawAccount.class);
        final var onlyDust = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/onlyDust.json"), RawAccount.class);
        final var anthonySocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users" +
                                                                                                        "/anthony_social_accounts.json"),
                RawSocialAccount[].class));
        final var pierreSocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre_social_accounts" +
                                                                                                       ".json"), RawSocialAccount[].class));
        final var olivierSocialAccounts = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users" +
                                                                                                        "/olivier_social_accounts.json"),
                RawSocialAccount[].class));

        // When
        final var response = indexPullRequest("onlydustxyz", "marketplace-frontend", 1257L, "ghp_GITHUB_USER_PAT");

        // Then
        response.expectStatus().isNoContent();

        assertThat(pullRequestsRepository.findAll()).containsExactly(RawPullRequestEntity.of(marketplaceFrontend.getId(), pr1257));
        assertThat(repoRepository.findAll()).containsExactly(RawRepoEntity.of(marketplaceFrontend));
        assertThat(pullRequestReviewsRepository.findAll()).containsExactly(RawPullRequestReviewEntity.of(pr1257.getId(), pr1257Reviews));
        assertThat(pullRequestsCommitsRepository.findAll()).containsExactly(RawPullRequestCommitsEntity.of(pr1257.getId(), details(pr1257Commits)));
        assertThat(userRepository.findAll()).containsExactlyInAnyOrder(RawUserEntity.of(pierre), RawUserEntity.of(olivier), RawUserEntity.of(anthony),
                RawUserEntity.of(onlyDust));
        assertThat(userSocialAccountsRepository.findAll()).containsExactlyInAnyOrder(
                RawUserSocialAccountsEntity.of(pierre.getId(), pierreSocialAccounts),
                RawUserSocialAccountsEntity.of(olivier.getId(), olivierSocialAccounts),
                RawUserSocialAccountsEntity.of(anthony.getId(), anthonySocialAccounts),
                RawUserSocialAccountsEntity.of(onlyDust.getId(), List.of())
        );
        assertThat(issueRepository.findAll()).containsExactly(RawIssueEntity.of(marketplaceFrontend.getId(), issue78));
        assertThat(pullRequestClosingIssueRepository.findAll()).containsExactly(
                RawPullRequestClosingIssuesEntity.of(marketplaceFrontend.getOwner().getLogin(), marketplaceFrontend.getName(), pr1257.getNumber(),
                        pr1257ClosingIssues));
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
        assertThat(githubPullRequest.getCreatedAt().toString()).isEqualTo("2023-09-21 12:42:45.0");
        assertThat(githubPullRequest.getCommitCount()).isEqualTo(1);
        assertThat(githubPullRequest.getMainFileExtensions()).containsExactly("sql");
        final var commitCounts = githubPullRequest.getCommitCounts().stream().findFirst().orElseThrow();
        assertThat(commitCounts.getPullRequestId()).isEqualTo(pr1257.getId());
        assertThat(commitCounts.getAuthor().getId()).isEqualTo(anthony.getId());
        assertThat(commitCounts.getCommitCount()).isEqualTo(1);
    }

    private List<RawCommit> details(List<RawCommit> commits) {
        return commits.stream().map(this::details).toList();
    }

    @SneakyThrows
    private RawCommit details(RawCommit commit) {
        return sanitized(mapper.readValue(getClass()
                        .getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/commits/%s.json".formatted(commit.getSha())),
                RawCommit.class));
    }

    private RawCommit sanitized(RawCommit commit) {
        return new RawCommit(commit.getSha(), commit.getAuthor(), commit.getCommitter(), commit.getFiles().stream().map(this::sanitized).toList());
    }

    private RawCommitFile sanitized(RawCommitFile file) {
        return new RawCommitFile(
                file.getSha(),
                file.getFilename(),
                file.getStatus(),
                file.getAdditions(),
                file.getDeletions(),
                file.getChanges(),
                ""
        );
    }

    @Test
    @Order(2)
    public void should_index_pull_request_with_multiple_commits_on_demand() throws IOException {
        // Given
        final var marketplaceFrontend = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend.json"),
                RawRepo.class);
        final var pr1258 = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls/1258.json"),
                RawPullRequest.class);
        final var pr1258Reviews = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls" +
                                                                                                "/1258_reviews.json"), RawCodeReview[].class));
        final var pr1258Commits = Arrays.asList(mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/repos/marketplace-frontend/pulls" +
                                                                                                "/1258_commits.json"), RawCommit[].class));

        final var anthony = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/anthony.json"), RawAccount.class);
        final var pierre = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/pierre.json"), RawAccount.class);
        final var olivier = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/olivier.json"), RawAccount.class);
        final var onlyDust = mapper.readValue(getClass().getResourceAsStream("/wiremock/github/__files/users/onlyDust.json"), RawAccount.class);

        // When
        final var response = indexPullRequest("onlydustxyz", "marketplace-frontend", 1258L, "GITHUB_PAT");

        // Then
        response.expectStatus().isNoContent();

        assertThat(pullRequestsRepository.findAll()).contains(RawPullRequestEntity.of(marketplaceFrontend.getId(), pr1258));
        assertThat(pullRequestReviewsRepository.findAll()).contains(RawPullRequestReviewEntity.of(pr1258.getId(), pr1258Reviews));
        assertThat(pullRequestsCommitsRepository.findAll()).contains(RawPullRequestCommitsEntity.of(pr1258.getId(), details(pr1258Commits)));
        assertThat(userRepository.findAll()).contains(RawUserEntity.of(pierre), RawUserEntity.of(olivier), RawUserEntity.of(anthony),
                RawUserEntity.of(onlyDust));

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
