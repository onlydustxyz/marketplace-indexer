package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawGithubAppEvent;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Builder
public class DiffRawStorageReaderDecorator implements RawStorageReader {
    private final RawStorageReader fetcher;
    private final RawStorageReader cache;

    @Override
    public Optional<RawRepo> repo(Long repoId) {
        return fetcher.repo(repoId);
    }

    @Override
    public Optional<RawRepo> repo(String repoOwner, String repoName) {
        return fetcher.repo(repoOwner, repoName);
    }

    @Override
    public Stream<RawPullRequest> repoPullRequests(Long repoId) {
        return fetcher.repoPullRequests(repoId)
                .filter(pullRequest -> cache.pullRequest(repoId, pullRequest.getNumber())
                        .map(cached -> !cached.getUpdatedAt().equals(pullRequest.getUpdatedAt()))
                        .orElse(true)
                );
    }

    @Override
    public Stream<RawIssue> repoIssues(Long repoId) {
        return fetcher.repoIssues(repoId)
                .filter(issue -> cache.issue(repoId, issue.getNumber())
                        .map(cached -> !cached.getUpdatedAt().equals(issue.getUpdatedAt()))
                        .orElse(true)
                );
    }

    @Override
    public Optional<RawLanguages> repoLanguages(Long repoId) {
        return fetcher.repoLanguages(repoId);
    }

    @Override
    public Optional<RawAccount> user(Long userId) {
        return fetcher.user(userId);
    }

    @Override
    public Optional<List<RawSocialAccount>> userSocialAccounts(Long userId) {
        return fetcher.userSocialAccounts(userId);
    }

    @Override
    public Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber) {
        return fetcher.pullRequest(repoId, prNumber);
    }

    @Override
    public Optional<RawIssue> issue(Long repoId, Long issueNumber) {
        return fetcher.issue(repoId, issueNumber);
    }

    @Override
    public Optional<List<RawCodeReview>> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return fetcher.pullRequestReviews(repoId, pullRequestId, pullRequestNumber);
    }

    @Override
    public Optional<List<RawCommit>> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber) {
        return fetcher.pullRequestCommits(repoId, pullRequestId, pullRequestNumber);
    }

    @Override
    public Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber) {
        return fetcher.pullRequestClosingIssues(repoOwner, repoName, pullRequestNumber);
    }

    @Override
    public Stream<RawGithubAppEvent> userEvents(Long userId, ZonedDateTime since) {
        return fetcher.userEvents(userId, since);
    }
}
