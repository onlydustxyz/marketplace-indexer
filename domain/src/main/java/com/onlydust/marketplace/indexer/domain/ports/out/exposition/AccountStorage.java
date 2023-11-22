package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;

public interface AccountStorage {
    void save(GithubAccount account);
}
