package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Stream;

import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;

public interface PublicEventRawStorageReader {
    Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since);

    Stream<RawPublicEvent> allPublicEvents(ZonedDateTime timestamp, List<Long> userIds);
}
