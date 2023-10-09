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
    public List<RawPullRequest> repoPullRequests(Long repoId) {
        final var pullRequests = fetcher.repoPullRequests(repoId);
        cache.saveRepoPullRequests(repoId, pullRequests);
        return pullRequests;
    }

    @Override
    public List<RawIssue> repoIssues(Long repoId) {
        final var issues = fetcher.repoIssues(repoId);
        cache.saveRepoIssues(repoId, issues);
        return issues;
    }

    @Override
    public RawLanguages repoLanguages(Long repoId) {
        final var languages = fetcher.repoLanguages(repoId);
        cache.saveRepoLanguages(repoId, languages);
        return languages;
    }

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
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        final var pullRequest = fetcher.pullRequest(repoId, prNumber);
        pullRequest.ifPresent(pr -> cache.savePullRequest(repoId, pr));
        return pullRequest;
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        final var issue = fetcher.issue(repoId, issueNumber);
        issue.ifPresent(i -> cache.saveIssue(repoId, i));
        return issue;
    }

    @Override
    public List<RawCodeReview> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        final var reviews = fetcher.pullRequestReviews(repoId, pullRequestId, pullRequestNumber);
        cache.savePullRequestReviews(pullRequestId, reviews);
        return reviews;
    }

    @Override
    public List<RawCommit> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        final var commits = fetcher.pullRequestCommits(repoId, pullRequestId, pullRequestNumber);
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
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        final var closingIssues = fetcher.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber);
        closingIssues.ifPresent(cache::saveClosingIssues);
        return closingIssues;
    }
}
