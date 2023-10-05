package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.model.SocialAccount;
import com.onlydust.marketplace.indexer.domain.model.User;

import java.util.List;

public interface RawStorageRepository extends RawStorageReader {
    void save(User user);

    void save(Integer userId, List<SocialAccount> socialAccounts);
}
