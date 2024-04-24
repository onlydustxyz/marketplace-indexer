package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import lombok.Builder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Builder
public class CacheReadRawStorageReaderDecorator implements RawStorageReader {
    private final RawStorageReader fetcher;
    private final RawStorageReader cache;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return cache.repo(repoId).or(() -> fetcher.repo(repoId));
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return cache.repo(repoOwner, repoName).or(() -> fetcher.repo(repoOwner, repoName));
    }

    @Override
    public Stream<RawPullRequest> repoPullRequests(Long repoId) {
        if (cache.repoPullRequests(repoId).anyMatch(pr -> true)) {
            return cache.repoPullRequests(repoId);
        } else {
            return fetcher.repoPullRequests(repoId);
        }
    }

    @Override
    public Stream<RawIssue> repoIssues(Long repoId) {
        if (cache.repoIssues(repoId).anyMatch(i -> true)) {
            return cache.repoIssues(repoId);
        } else {
            return fetcher.repoIssues(repoId);
        }
    }

    @Override
    public Optional<RawLanguages> repoLanguages(Long repoId) {
        return cache.repoLanguages(repoId).or(() -> fetcher.repoLanguages(repoId));
    }

    @Override
    public Optional<RawAccount> user(Long userId) {
        return cache.user(userId).or(() -> fetcher.user(userId));
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccounts(Long userId) {
        return cache.userSocialAccounts(userId).or(() -> fetcher.userSocialAccounts(userId));
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
    public Optional<List<RawCodeReview>> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return cache.pullRequestReviews(repoId, pullRequestId, pullRequestNumber)
                .or(() -> fetcher.pullRequestReviews(repoId, pullRequestId, pullRequestNumber));
    }

    @Override
    public Optional<List<RawCommit>> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return cache.pullRequestCommits(repoId, pullRequestId, pullRequestNumber)
                .or(() -> fetcher.pullRequestCommits(repoId, pullRequestId, pullRequestNumber));
    }

    @Override
    public Optional<RawPullRequestDiff> pullRequestDiff(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return cache.pullRequestDiff(repoId, pullRequestId, pullRequestNumber)
                .or(() -> fetcher.pullRequestDiff(repoId, pullRequestId, pullRequestNumber));
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return cache.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber)
                .or(() -> fetcher.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber));
    }
}
