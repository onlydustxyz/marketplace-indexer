package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import lombok.NonNull;

public interface UserStatsIndexer {
    void indexUser(final @NonNull Long userId);
}
