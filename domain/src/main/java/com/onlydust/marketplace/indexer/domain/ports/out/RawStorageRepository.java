package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.*;

import java.util.List;

public interface RawStorageRepository extends RawStorageReader {
    void saveUser(RawUser user);

    void saveUserSocialAccounts(Long userId, List<RawSocialAccount> socialAccounts);

    void savePullRequest(RawPullRequest pullRequest);

    void savePullRequestReviews(Long pullRequestId, List<RawCodeReview> codeReview);

    void savePullRequestCommits(Long pullRequestId, List<RawCommit> commits);

    void saveCheckRuns(Long repoId, String sha, RawCheckRuns checkRuns);

    void saveIssue(RawIssue issue);
}
