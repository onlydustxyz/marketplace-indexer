package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.public_events.PublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserStatsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Slf4j
public class UserStatsIndexingService implements UserStatsIndexer {
    private final RawStorageReader rawStorageReader;


    @Override
    public void indexUser(final @NonNull Long userId) {
        rawStorageReader.user(userId)
                .ifPresentOrElse(user -> indexUser(user.getId(), user.getCreatedAt()),
                        () -> LOGGER.warn("User {} not found", userId));
    }

    private void indexUser(final @NonNull Long userId, final @NonNull ZonedDateTime since) {
        LOGGER.debug("Indexing stats for user {} since {}", userId, since);
        rawStorageReader.userPublicEvents(userId, since)
                .flatMap(event -> PublicEvent.of(event).stream())
                .forEach(event -> LOGGER.debug("Event: {}", event));
    }
}
