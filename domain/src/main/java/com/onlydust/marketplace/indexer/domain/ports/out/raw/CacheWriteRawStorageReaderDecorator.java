package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import lombok.Builder;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Builder
public class CacheWriteRawStorageReaderDecorator implements RawStorageReader {
    private final RawStorageReader fetcher;
    private final RawStorageWriter cache;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        final var repo = fetcher.repo(repoId);
        repo.ifPresent(cache::saveRepo);
        return repo;
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        final var repo = fetcher.repo(repoOwner, repoName);
        repo.ifPresent(cache::saveRepo);
        return repo;
    }

    @Override
    public Stream<RawPullRequest> repoPullRequests(Long repoId) {
        return fetcher.repoPullRequests(repoId);
    }

    @Override
    public Stream<RawIssue> repoIssues(Long repoId) {
        return fetcher.repoIssues(repoId);
    }

    @Override
    public Optional<RawLanguages> repoLanguages(Long repoId) {
        final var languages = fetcher.repoLanguages(repoId);
        languages.ifPresent(l -> cache.saveRepoLanguages(repoId, l));
        return languages;
    }

    @Override
    public Optional<RawAccount> user(Long userId) {
        final var user = fetcher.user(userId);
        user.ifPresent(cache::saveUser);
        return user;
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccounts(Long userId) {
        final var socialAccounts = fetcher.userSocialAccounts(userId);
        socialAccounts.ifPresent(accounts -> cache.saveUserSocialAccounts(userId, accounts));
        return socialAccounts;
    }

    @Override
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        final var pullRequest = fetcher.pullRequest(repoId, prNumber);
        pullRequest.ifPresent(cache::savePullRequest);
        return pullRequest;
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        final var issue = fetcher.issue(repoId, issueNumber);
        issue.ifPresent(i -> cache.saveIssue(repoId, i));
        return issue;
    }

    @Override
    public Optional<List<RawCodeReview>> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        final var reviews = fetcher.pullRequestReviews(repoId, pullRequestId, pullRequestNumber);
        reviews.ifPresent(r -> cache.savePullRequestReviews(pullRequestId, r));
        return reviews;
    }

    @Override
    public Optional<List<RawCommit>> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        final var commits = fetcher.pullRequestCommits(repoId, pullRequestId, pullRequestNumber);
        commits.ifPresent(c -> cache.savePullRequestCommits(pullRequestId, c));
        return commits;
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        final var closingIssues = fetcher.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber);
        closingIssues.ifPresent(data -> cache.saveClosingIssues(repoOwner, repoName, pullRequestNumber, data));
        return closingIssues;
    }
}
