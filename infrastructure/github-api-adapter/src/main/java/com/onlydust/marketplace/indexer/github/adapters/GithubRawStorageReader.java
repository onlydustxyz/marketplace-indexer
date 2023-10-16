package com.onlydust.marketplace.indexer.github.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import com.onlydust.marketplace.indexer.github.GithubPage;
import lombok.AllArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class GithubRawStorageReader implements RawStorageReader {
    private final GithubHttpClient client;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return client.get("/repositories/" + repoId, RawRepo.class);
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return client.get("/repos/" + repoOwner + "/" + repoName, RawRepo.class);
    }

    @Override
    public List<RawPullRequest> repoPullRequests(Long repoId) {
        final var page = new GithubPage<>(client, "/repositories/" + repoId + "/pulls?state=all&sort=updated&per_page=100", RawPullRequest[].class);
        List<RawPullRequest> pullRequests = new ArrayList<>();
        page.forEachRemaining(pullRequests::add);
        return pullRequests;
    }

    @Override
    public List<RawIssue> repoIssues(Long repoId) {
        final var page = new GithubPage<>(client, "/repositories/" + repoId + "/issues?state=all&sort=updated&per_page=100", RawIssue[].class);
        List<RawIssue> issues = new ArrayList<>();
        page.forEachRemaining(issues::add);
        return issues;
    }

    @Override
    public RawLanguages repoLanguages(Long repoId) {
        return client.get("/repositories/" + repoId + "/languages", RawLanguages.class)
                .orElse(new RawLanguages());
    }

    @Override
    public Optional<RawUser> user(Long userId) {
        return client.get("/user/" + userId, RawUser.class);
    }

    @Override
    public List<RawSocialAccount> userSocialAccounts(Long userId) {
        return client.get("/user/" + userId + "/social_accounts", RawSocialAccount[].class)
                .map(Arrays::asList)
                .orElse(List.of());
    }

    @Override
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        return client.get("/repositories/" + repoId + "/pulls/" + prNumber, RawPullRequest.class);
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        return client.get("/repositories/" + repoId + "/issues/" + issueNumber, RawIssue.class);
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Long repoId, Long pullRequestId, Long prNumber) {
        return client.get("/repositories/" + repoId + "/pulls/" + prNumber + "/reviews", RawCodeReview[].class)
                .map(Arrays::asList)
                .orElse(List.of());
    }

    @Override
    public List<RawCommit> pullRequestCommits(Long repoId, Long pullRequestId, Long prNumber) {
        return client.get("/repositories/" + repoId + "/pulls/" + prNumber + "/commits", RawCommit[].class)
                .map(Arrays::asList)
                .orElse(List.of());
    }

    @Override
    public Optional<RawCheckRuns> checkRuns(Long repoId, String sha) {
        return client.get("/repositories/" + repoId + "/commits/" + sha + "/check-runs", RawCheckRuns.class);
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return Optional.empty(); // TODO
    }
}
