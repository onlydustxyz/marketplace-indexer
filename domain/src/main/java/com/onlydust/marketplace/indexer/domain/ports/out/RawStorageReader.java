package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.RawCodeReview;
import com.onlydust.marketplace.indexer.domain.model.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;

import java.util.List;
import java.util.Optional;

public interface RawStorageReader {
    Optional<RawUser> user(Integer userId);

    List<RawSocialAccount> userSocialAccounts(Integer userId);

    Optional<RawPullRequest> pullRequest(String repoOwner, String repoName, Integer prNumber);

    List<RawCodeReview> pullRequestReviews(Integer pullRequestId);
}
