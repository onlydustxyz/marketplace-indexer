package com.onlydust.marketplace.indexer.domain.jobs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.RawEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EventsInboxJobTest {
    private final EventInboxStorage eventInboxStorage = mock(EventInboxStorage.class);
    private final EventHandler<RawInstallationEvent> installationEventHandler = mock(EventHandler.class);
    private final InstallationEventsInboxJob eventsInboxJob = new InstallationEventsInboxJob(eventInboxStorage, installationEventHandler);
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    public void should_ack_correct_events() {
        final var events = new RawEvent[]{
                new RawEvent(1L, "installation", mapper.createObjectNode()),
                new RawEvent(2L, "installation", mapper.createObjectNode()),
                new RawEvent(3L, "installation", mapper.createObjectNode()),
        };

        when(eventInboxStorage.peek("installation", "installation_repositories"))
                .thenReturn(Optional.of(events[0]))
                .thenReturn(Optional.of(events[1]))
                .thenReturn(Optional.of(events[2]))
                .thenReturn(Optional.empty());

        eventsInboxJob.run();

        verify(installationEventHandler, times(3)).process(any());
        verify(eventInboxStorage).ack(1L);
        verify(eventInboxStorage).ack(2L);
        verify(eventInboxStorage).ack(3L);
    }

    @Test
    public void should_nack_wrong_events() {
        doThrow(new RuntimeException("Error deserializing event payload"))
                .when(installationEventHandler).process(any());

        when(eventInboxStorage.peek("installation", "installation_repositories"))
                .thenReturn(Optional.of(new RawEvent(1L, "installation", mapper.createObjectNode())))
                .thenReturn(Optional.empty());

        eventsInboxJob.run();

        verify(eventInboxStorage).nack(1L, "Error deserializing event payload");
    }
}