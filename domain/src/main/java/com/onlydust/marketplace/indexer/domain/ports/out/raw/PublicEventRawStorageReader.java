package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

public interface PublicEventRawStorageReader {
    Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since);

    Stream<RawPublicEvent> allPublicEvents(ZonedDateTime timestamp);
}
