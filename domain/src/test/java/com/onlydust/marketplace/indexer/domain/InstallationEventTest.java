package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.EventListener;
import com.onlydust.marketplace.indexer.domain.ports.out.EventListenerComposite;
import com.onlydust.marketplace.indexer.domain.services.EventProcessorService;
import com.onlydust.marketplace.indexer.domain.stubs.EventListenerStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawInstallationEventRepositoryStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageRepositoryStub;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class InstallationEventTest {
    private final RawInstallationEventRepositoryStub rawInstallationEventRepositoryStub = new RawInstallationEventRepositoryStub();
    private final RawInstallationEvent newInstallationEvent = RawStorageRepositoryStub.load("/github/events/new_installation.json", RawInstallationEvent.class);
    private final EventListenerStub<InstallationEvent> installationEventEventListenerStub = new EventListenerStub<>();
    private final RawStorageRepositoryStub rawStorageRepositoryStub = new RawStorageRepositoryStub();
    private final EventListener<InstallationEvent> eventListener = new EventListenerComposite<>(List.of(installationEventEventListenerStub, installationEventEventListenerStub));
    private final EventProcessorService eventProcessorService = new EventProcessorService(rawInstallationEventRepositoryStub, eventListener, rawStorageRepositoryStub);


    @Test
    public void should_store_raw_events() {
        eventProcessorService.process(newInstallationEvent);

        assertCachedEventsAre(newInstallationEvent);
        assertThat(installationEventEventListenerStub.events()).hasSize(2);
    }

    private void assertCachedEventsAre(RawInstallationEvent... events) {
        assertThat(rawInstallationEventRepositoryStub.events()).containsExactly(events);
    }
}
