package com.onlydust.marketplace.indexer.github.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import lombok.AllArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class GithubRawStorageReader implements RawStorageReader {
    private final GithubHttpClient client;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return Optional.of(client.get("/repositories/" + repoId, RawRepo.class));
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return Optional.of(client.get("/repos/" + repoOwner + "/" + repoName, RawRepo.class));
    }

    @Override
    public List<RawPullRequest> repoPullRequests(Long repoId) {
        return Arrays.stream(client.get("/repositories/" + repoId + "/pulls", RawPullRequest[].class)).toList();
    }

    @Override
    public List<RawIssue> repoIssues(Long repoId) {
        return Arrays.stream(client.get("/repositories/" + repoId + "/issues", RawIssue[].class)).toList();
    }

    @Override
    public RawLanguages repoLanguages(Long repoId) {
        return client.get("/repositories/" + repoId + "/languages", RawLanguages.class);
    }

    @Override
    public Optional<RawUser> user(Long userId) {
        return Optional.of(client.get("/user/" + userId, RawUser.class));
    }

    @Override
    public List<RawSocialAccount> userSocialAccounts(Long userId) {
        return Arrays.stream(client.get("/user/" + userId + "/social_accounts", RawSocialAccount[].class)).toList();
    }

    @Override
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        return Optional.of(client.get("/repositories/" + repoId + "/pulls/" + prNumber, RawPullRequest.class));
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        return Optional.of(client.get("/repositories/" + repoId + "/issues/" + issueNumber, RawIssue.class));
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Long repoId, Long pullRequestId, Long prNumber) {
        return Arrays.stream(client.get("/repositories/" + repoId + "/pulls/" + prNumber + "/reviews", RawCodeReview[].class)).toList();
    }

    @Override
    public List<RawCommit> pullRequestCommits(Long repoId, Long pullRequestId, Long prNumber) {
        return Arrays.stream(client.get("/repositories/" + repoId + "/pulls/" + prNumber + "/commits", RawCommit[].class)).toList();
    }

    @Override
    public RawCheckRuns checkRuns(Long repoId, String sha) {
        return client.get("/repositories/" + repoId + "/commits/" + sha + "/check-runs", RawCheckRuns.class);
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return Optional.empty(); // TODO
    }
}
