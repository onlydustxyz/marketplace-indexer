package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.RawCodeReview;
import com.onlydust.marketplace.indexer.domain.model.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;

import java.util.List;

public interface RawStorageRepository extends RawStorageReader {
    void saveUser(RawUser user);

    void saveUserSocialAccounts(Integer userId, List<RawSocialAccount> socialAccounts);

    void savePullRequest(RawPullRequest pullRequest);

    void savePullRequestReviews(Integer pullRequestId, List<RawCodeReview> codeReview);
}
