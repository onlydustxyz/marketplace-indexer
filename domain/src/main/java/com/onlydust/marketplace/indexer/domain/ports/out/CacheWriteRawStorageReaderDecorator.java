package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.*;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
public class CacheWriteRawStorageReaderDecorator implements RawStorageReader {
    private final RawStorageReader fetcher;
    private final RawStorageRepository cache;

    @Override
    public Optional<RawUser> user(Integer userId) {
        final var user = fetcher.user(userId);
        user.ifPresent(cache::saveUser);
        return user;
    }

    @Override
    public List<RawSocialAccount> userSocialAccounts(Integer userId) {
        final var socialAccounts = fetcher.userSocialAccounts(userId);
        cache.saveUserSocialAccounts(userId, socialAccounts);
        return socialAccounts;
    }

    @Override
    public Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Integer prNumber) {
        final var pullRequest = fetcher.pullRequest(repoOwner, repoName, prNumber);
        pullRequest.ifPresent(cache::savePullRequest);
        return pullRequest;
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Integer pullRequestId) {
        final var reviews = fetcher.pullRequestReviews(pullRequestId);
        cache.savePullRequestReviews(pullRequestId, reviews);
        return reviews;
    }

    @Override
    public List<RawCommit> pullRequestCommits(Integer pullRequestId) {
        final var commits = fetcher.pullRequestCommits(pullRequestId);
        cache.savePullRequestCommits(pullRequestId, commits);
        return commits;
    }
}
