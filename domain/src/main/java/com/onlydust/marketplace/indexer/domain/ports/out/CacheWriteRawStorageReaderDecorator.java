package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CacheWriteRawStorageReaderDecorator implements RawStorageReader {
    private final RawStorageReader fetcher;
    private final RawStorageRepository cache;

    @Override
    public Optional<RawUser> user(Long userId) {
        final var user = fetcher.user(userId);
        user.ifPresent(cache::saveUser);
        return user;
    }

    @Override
    public List<RawSocialAccount> userSocialAccounts(Long userId) {
        final var socialAccounts = fetcher.userSocialAccounts(userId);
        cache.saveUserSocialAccounts(userId, socialAccounts);
        return socialAccounts;
    }

    @Override
    public Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Long prNumber) {
        final var pullRequest = fetcher.pullRequest(repoOwner, repoName, prNumber);
        pullRequest.ifPresent(cache::savePullRequest);
        return pullRequest;
    }

    @Override
    public Optional<RawIssue> issue(String repoOwner, String repoName, Long issueNumber) {
        final var issue = fetcher.issue(repoOwner, repoName, issueNumber);
        issue.ifPresent(cache::saveIssue);
        return issue;
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Long pullRequestId) {
        final var reviews = fetcher.pullRequestReviews(pullRequestId);
        cache.savePullRequestReviews(pullRequestId, reviews);
        return reviews;
    }

    @Override
    public List<RawCommit> pullRequestCommits(Long pullRequestId) {
        final var commits = fetcher.pullRequestCommits(pullRequestId);
        cache.savePullRequestCommits(pullRequestId, commits);
        return commits;
    }

    @Override
    public RawCheckRuns checkRuns(Long repoId, String sha) {
        final var checkRuns = fetcher.checkRuns(repoId, sha);
        cache.saveCheckRuns(repoId, sha, checkRuns);
        return checkRuns;
    }

    @Override
    public List<Long> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        final var issueNumbers = fetcher.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber);
        cache.saveClosingIssues(repoOwner, repoName, pullRequestNumber, issueNumbers);
        return issueNumbers;
    }
}
