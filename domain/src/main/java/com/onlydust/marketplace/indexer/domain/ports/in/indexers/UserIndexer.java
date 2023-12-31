package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;

import java.util.Optional;

public interface UserIndexer {
    Optional<CleanAccount> indexUser(Long userId);
}
