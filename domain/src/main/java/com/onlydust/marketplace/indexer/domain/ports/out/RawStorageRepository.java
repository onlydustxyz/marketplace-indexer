package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;

import java.util.List;

public interface RawStorageRepository extends RawStorageReader {
    void save(RawUser user);

    void save(Integer userId, List<RawSocialAccount> socialAccounts);

    void save(RawPullRequest pullRequest);
}
