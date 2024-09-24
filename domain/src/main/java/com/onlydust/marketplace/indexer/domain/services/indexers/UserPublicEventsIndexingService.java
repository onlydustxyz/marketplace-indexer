package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.events.GithubAppEvent;
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
    public Stream<GithubAppEvent> indexUser(Long userId, ZonedDateTime since) {
        LOGGER.debug("Indexing stats for user {} since {}", userId, since);
        return rawStorageReader.userEvents(userId, since).flatMap(event -> GithubAppEvent.of(event).stream());
    }
}
