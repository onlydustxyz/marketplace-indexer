package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import lombok.NonNull;

import java.time.ZonedDateTime;
import java.util.Set;

public interface UserPublicEventsIndexer {
    void indexUser(final @NonNull Long userId, final @NonNull ZonedDateTime since);

    void indexUsers(final @NonNull Set<Long> userIds, final @NonNull ZonedDateTime since);
}
