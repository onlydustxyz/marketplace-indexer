package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.services.EventProcessorService;
import com.onlydust.marketplace.indexer.domain.stubs.EventListenerStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawInstallationEventRepositoryStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageRepositoryStub;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InstallationEventTest {
    private final RawInstallationEventRepositoryStub rawInstallationEventRepositoryStub = new RawInstallationEventRepositoryStub();
    private final RawInstallationEvent newInstallationEvent = RawStorageRepositoryStub.load("/github/events/new_installation.json", RawInstallationEvent.class);
    private final EventListenerStub<InstallationEvent> installationEventEventListenerStub = new EventListenerStub<>();
    private final RawStorageRepositoryStub rawStorageRepositoryStub = new RawStorageRepositoryStub();
    private final EventProcessorService eventProcessorService = new EventProcessorService(rawInstallationEventRepositoryStub, installationEventEventListenerStub, rawStorageRepositoryStub);


    @Test
    public void should_store_raw_events() {
        eventProcessorService.process(newInstallationEvent);

        assertCachedEventsAre(newInstallationEvent);
        assertThat(installationEventEventListenerStub.events()).hasSize(1);
    }

    private void assertCachedEventsAre(RawInstallationEvent... events) {
        assertThat(rawInstallationEventRepositoryStub.events()).containsExactly(events);
    }
}
