package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.*;

import java.util.List;

public interface RawStorageWriter {
    void saveUser(RawAccount user);

    void saveUserSocialAccounts(Long userId, List<RawSocialAccount> socialAccounts);

    void savePullRequest(Long repoId, RawPullRequest pullRequest);

    void savePullRequestReviews(Long pullRequestId, List<RawCodeReview> codeReview);

    void savePullRequestCommits(Long pullRequestId, List<RawCommit> commits);
    
    void saveIssue(Long repoId, RawIssue issue);

    void saveRepo(RawRepo repo);

    void deleteRepo(Long repoId);

    void saveRepoLanguages(Long repoId, RawLanguages languages);

    void saveClosingIssues(String repoOwner, String repoName, Long pullRequestNumber, RawPullRequestClosingIssues closingIssues);
}
