package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.User;

public interface UserIndexer {
    User indexUser(Long userId);
}
