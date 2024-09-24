package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

@Builder
public class CacheWritePublicEventRawStorageReaderDecorator implements PublicEventRawStorageReader {
    private final PublicEventRawStorageReader fetcher;
    private final PublicEventRawStorageWriter cache;

    @Override
    public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
        return fetcher.userPublicEvents(userId, since)
                .peek(cache::savePublicEvent);
    }
}
