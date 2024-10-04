package com.onlydust.marketplace.indexer.domain.services.readers;

import com.github.javafaker.Faker;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.PublicEventRawStorageReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import static org.mockito.Mockito.*;

class PublicEventRawStorageReaderAggregatorTest {
    private final PublicEventRawStorageReader archivedReader = mock(PublicEventRawStorageReader.class);
    private final PublicEventRawStorageReader liveReader = mock(PublicEventRawStorageReader.class);
    private final PublicEventRawStorageReaderAggregator aggregator = new PublicEventRawStorageReaderAggregator(archivedReader, liveReader);

    private final Faker faker = new Faker();
    private final Long userId = faker.number().randomNumber();

    @BeforeEach
    void setUp() {
        reset(archivedReader, liveReader);
    }

    @Test
    void should_fetch_only_live_events_if_recent() {
        // Given
        final var since = ZonedDateTime.now().minusSeconds(1);

        // When
        aggregator.userPublicEvents(userId, since);

        // Then
        verify(liveReader).userPublicEvents(userId, since);
        verifyNoInteractions(archivedReader);
    }


    @Test
    void should_fetch_archived_and_live_events_if_not_recent() {
        // Given
        final var since = ZonedDateTime.now().minusDays(5);

        // When
        aggregator.userPublicEvents(userId, since);

        // Then
        verify(archivedReader).userPublicEvents(userId, since);
        verify(liveReader).userPublicEvents(userId, ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS));
    }
}