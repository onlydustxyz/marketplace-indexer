package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

@Builder
public class CacheReadPublicEventRawStorageReaderDecorator implements PublicEventRawStorageReader {
    private final PublicEventRawStorageReader fetcher;
    private final PublicEventRawStorageReader cache;

    @Override
    public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
        if (cache.userPublicEvents(userId, since).anyMatch(e -> true)) {
            return cache.userPublicEvents(userId, since);
        } else {
            return fetcher.userPublicEvents(userId, since);
        }
    }
}
