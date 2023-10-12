package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

@Builder
public class CacheReadRawStorageReaderDecorator implements RawStorageReader {
    private final RawStorageReader fetcher;
    private final RawStorageRepository cache;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return cache.repo(repoId).or(() -> fetcher.repo(repoId));
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return cache.repo(repoOwner, repoName).or(() -> fetcher.repo(repoOwner, repoName));
    }

    @Override
    public List<RawPullRequest> repoPullRequests(Long repoId) {
        final var pullRequests = cache.repoPullRequests(repoId);
        if (!pullRequests.isEmpty()) {
            return pullRequests;
        } else {
            return fetcher.repoPullRequests(repoId);
        }
    }

    @Override
    public List<RawIssue> repoIssues(Long repoId) {
        final var issues = cache.repoIssues(repoId);
        if (!issues.isEmpty()) {
            return issues;
        } else {
            return fetcher.repoIssues(repoId);
        }
    }

    @Override
    public RawLanguages repoLanguages(Long repoId) {
        final var languages = cache.repoLanguages(repoId);
        if (!languages.isEmpty()) {
            return languages;
        } else {
            return fetcher.repoLanguages(repoId);
        }
    }

    @Override
    public Optional<RawUser> user(Long userId) {
        return cache.user(userId).or(() -> fetcher.user(userId));
    }

    @Override
    public List<RawSocialAccount> userSocialAccounts(Long userId) {
        final var socialAccounts = cache.userSocialAccounts(userId);
        if (!socialAccounts.isEmpty()) {
            return socialAccounts;
        } else {
            return fetcher.userSocialAccounts(userId);
        }
    }

    @Override
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        return cache.pullRequest(repoId, prNumber).or(() -> fetcher.pullRequest(repoId, prNumber));
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        return cache.issue(repoId, issueNumber).or(() -> fetcher.issue(repoId, issueNumber));
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        final var reviews = cache.pullRequestReviews(repoId, pullRequestId, pullRequestNumber);
        if (!reviews.isEmpty()) {
            return reviews;
        } else {
            return fetcher.pullRequestReviews(repoId, pullRequestId, pullRequestNumber);
        }
    }

    @Override
    public List<RawCommit> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        final var commits = cache.pullRequestCommits(repoId, pullRequestId, pullRequestNumber);
        if (!commits.isEmpty()) {
            return commits;
        } else {
            return fetcher.pullRequestCommits(repoId, pullRequestId, pullRequestNumber);
        }
    }

    @Override
    public Optional<RawCheckRuns> checkRuns(Long repoId, String sha) {
        return cache.checkRuns(repoId, sha).or(() -> fetcher.checkRuns(repoId, sha));
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return cache.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber).or(() -> fetcher.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber));
    }
}
