package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.*;

import java.util.List;

public interface RawStorageRepository extends RawStorageReader {
    void saveUser(RawUser user);

    void saveUserSocialAccounts(Long userId, List<RawSocialAccount> socialAccounts);

    void savePullRequest(Long repoId, RawPullRequest pullRequest);

    void savePullRequestReviews(Long pullRequestId, List<RawCodeReview> codeReview);

    void savePullRequestCommits(Long pullRequestId, List<RawCommit> commits);

    void saveCheckRuns(Long repoId, String sha, RawCheckRuns checkRuns);

    void saveIssue(Long repoId, RawIssue issue);

    void saveRepo(RawRepo repo);

    void saveRepoPullRequests(Long repoId, List<RawPullRequest> pullRequests);

    void saveRepoIssues(Long repoId, List<RawIssue> issues);

    void saveRepoLanguages(Long repoId, RawLanguages languages);

    void saveClosingIssues(RawPullRequestClosingIssues closingIssues);
}
