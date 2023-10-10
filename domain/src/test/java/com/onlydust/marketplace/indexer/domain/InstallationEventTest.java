package com.onlydust.marketplace.indexer.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.services.EventProcessorService;
import com.onlydust.marketplace.indexer.domain.stubs.RawInstallationEventRepositoryStub;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

public class InstallationEventTest {
    private final RawInstallationEventRepositoryStub rawInstallationEventRepositoryStub = new RawInstallationEventRepositoryStub();

    private final RawInstallationEvent newInstallationEvent = load("/github/events/new_installation.json", RawInstallationEvent.class);

    private final EventProcessorService eventProcessorService = new EventProcessorService(rawInstallationEventRepositoryStub);

    private static <T> T load(String path, Class<T> type) {
        final var inputStream = type.getResourceAsStream(path);
        try {
            return new ObjectMapper().readValue(inputStream, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void should_store_raw_events() {
        eventProcessorService.process(newInstallationEvent);

        assertCachedEventsAre(newInstallationEvent);
    }

    private void assertCachedEventsAre(RawInstallationEvent... events) {
        assertThat(rawInstallationEventRepositoryStub.events()).containsExactly(events);
    }
}
