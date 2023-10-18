package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;

public interface UserIndexer {
    CleanAccount indexUser(Long userId);
}
