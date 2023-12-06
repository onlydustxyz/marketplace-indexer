package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.models.raw.RawEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class InstallationEventsInboxJob extends Job {
    private final EventInboxStorage eventInboxStorage;
    private final EventHandler<RawInstallationEvent> installationEventHandler;

    @Override
    protected void execute() {
        Optional<RawEvent> event;
        while ((event = eventInboxStorage.peek("installation", "installation_repositories")).isPresent())
            process(event.get());
    }

    private void process(RawEvent event) {
        try {
            installationEventHandler.process(event.payload(RawInstallationEvent.class));
            eventInboxStorage.ack(event.getId());
        } catch (Exception e) {
            LOGGER.error("Error processing event: {}", event.toString(), e);
            eventInboxStorage.nack(event.getId(), e.getMessage() == null ? e.toString() : e.getMessage());
        }
    }

    @Override
    public String name() {
        return "installation-events-inbox-dequeuer";
    }
}
