package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.*;

import java.util.List;
import java.util.Optional;

public interface RawStorageReader {
    Optional<RawUser> user(Integer userId);

    List<RawSocialAccount> userSocialAccounts(Integer userId);

    Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Integer prNumber);

    List<RawCodeReview> pullRequestReviews(Integer pullRequestId);

    List<RawCommit> pullRequestCommits(Integer pullRequestId);
}
