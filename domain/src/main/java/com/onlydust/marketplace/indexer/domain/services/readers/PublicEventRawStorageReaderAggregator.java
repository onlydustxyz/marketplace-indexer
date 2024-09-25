package com.onlydust.marketplace.indexer.domain.services.readers;

import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import lombok.AllArgsConstructor;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

@AllArgsConstructor
public class PublicEventRawStorageReaderAggregator implements PublicEventRawStorageReader {
    private final PublicEventRawStorageReader archivedReader;
    private final PublicEventRawStorageReader liveReader;

    @Override
    public Stream<RawPublicEvent> userPublicEvents(Long userId, ZonedDateTime since) {
        final var today = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS);
        return since.isAfter(today) ?
                liveReader.userPublicEvents(userId, since) :
                Stream.concat(
                        archivedReader.userPublicEvents(userId, since),
                        liveReader.userPublicEvents(userId, today)
                );
    }
}
