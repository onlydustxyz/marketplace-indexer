package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.raw.RawSocialAccount;
import com.onlydust.marketplace.indexer.domain.model.raw.RawUser;

import java.util.List;

public interface RawStorageRepository extends RawStorageReader {
    void save(RawUser rawUser);

    void save(Integer userId, List<RawSocialAccount> rawSocialAccounts);
}
