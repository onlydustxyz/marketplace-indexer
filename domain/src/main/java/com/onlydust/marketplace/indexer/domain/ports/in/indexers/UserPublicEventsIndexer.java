package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import lombok.NonNull;

import java.time.ZonedDateTime;

public interface UserPublicEventsIndexer {
    void indexUser(final @NonNull Long userId, final @NonNull ZonedDateTime since);

    void indexAllUsers(final @NonNull ZonedDateTime timestamp);
}
