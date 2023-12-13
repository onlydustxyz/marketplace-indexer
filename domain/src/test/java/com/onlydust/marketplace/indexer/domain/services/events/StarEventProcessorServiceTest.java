package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.raw.RawStarEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class StarEventProcessorServiceTest {
    final RepoIndexer repoIndexer = mock(RepoIndexer.class);
    final StarEventProcessorService starEventProcessorService = new StarEventProcessorService(repoIndexer);

    @Test
    void should_handle_star_event() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/star/cairo-streams-starred.json", RawStarEvent.class);

        // When
        starEventProcessorService.process(event);

        // Then
        verify(repoIndexer).indexRepo(493795808L);
    }
}