package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.*;

import java.util.List;

public interface RawStorageRepository extends RawStorageReader {
    void saveUser(RawUser user);

    void saveUserSocialAccounts(Integer userId, List<RawSocialAccount> socialAccounts);

    void savePullRequest(RawPullRequest pullRequest);

    void savePullRequestReviews(Integer pullRequestId, List<RawCodeReview> codeReview);

    void savePullRequestCommits(Integer pullRequestId, List<RawCommit> commits);
}
