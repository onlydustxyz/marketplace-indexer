package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.*;

import java.util.List;
import java.util.Optional;

public interface RawStorageReader {
    Optional<RawRepo> repo(Long repoId);

    Optional<RawRepo> repo(String repoOwner, String repoName);

    List<RawPullRequest> repoPullRequests(Long repoId);

    List<RawIssue> repoIssues(Long repoId);

    RawLanguages repoLanguages(Long repoId);

    Optional<RawAccount> user(Long userId);

    List<RawSocialAccount> userSocialAccounts(Long userId);

    Optional<RawPullRequest> pullRequest(Long repoId, Long prNumber);

    Optional<RawIssue> issue(Long repoId, Long issueNumber);

    List<RawCodeReview> pullRequestReviews(Long repoId, Long pullRequestId, Long pullRequestNumber);

    List<RawCommit> pullRequestCommits(Long repoId, Long pullRequestId, Long pullRequestNumber);

    Optional<RawCheckRuns> checkRuns(Long repoId, String sha);

    Optional<RawPullRequestClosingIssues> pullRequestClosingIssues(String repoOwner, String repoName, Long pullRequestNumber);
}
