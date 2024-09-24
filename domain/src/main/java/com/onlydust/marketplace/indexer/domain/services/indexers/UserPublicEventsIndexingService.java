package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.public_events.PublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

@AllArgsConstructor
@Slf4j
public class UserPublicEventsIndexingService implements UserPublicEventsIndexer {
    private final RawStorageReader rawStorageReader;

    @Override
    public Stream<PublicEvent> indexUser(Long userId, ZonedDateTime since) {
        LOGGER.debug("Indexing stats for user {} since {}", userId, since);
        return rawStorageReader.userPublicEvents(userId, since).flatMap(event -> PublicEvent.of(event).stream());
    }
}
