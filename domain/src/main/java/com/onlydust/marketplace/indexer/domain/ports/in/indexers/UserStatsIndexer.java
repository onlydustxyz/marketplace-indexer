package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import lombok.NonNull;

import java.time.ZonedDateTime;

public interface UserStatsIndexer {
    void indexUser(final @NonNull Long userId, final @NonNull ZonedDateTime since);
}
