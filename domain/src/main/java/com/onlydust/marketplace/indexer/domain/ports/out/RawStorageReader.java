package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface RawStorageReader {
    Optional<RawRepo> repo(Long repoId);

    Optional<RawRepo> repo(String repoOwner, String repoName);

    Stream<RawPullRequest> repoPullRequests(Long repoId);

    Stream<RawIssue> repoIssues(Long repoId);

    Optional<RawLanguages> repoLanguages(Long repoId);

    Optional<RawAccount> user(Long userId);

    Optional<List<RawSocialAccount>> userSocialAccounts(Long userId);

    Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber);

    Optional<RawIssue> issue(Long repoId, Long issueNumber);

    Optional<List<RawCodeReview>> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber);

    Optional<List<RawCommit>> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber);

    Optional<RawCheckRuns> checkRuns(Long repoId, String sha);

    Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber);
}
