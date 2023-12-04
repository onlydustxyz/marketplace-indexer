package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.models.raw.RawEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class EventsInboxJob extends Job {
    private final EventInboxStorage eventInboxStorage;
    private final EventHandler<RawInstallationEvent> installationEventHandler;
    private final EventHandler<RawRepositoryEvent> repositoryEventHandler;

    @Override
    protected void execute() {
        Optional<RawEvent> event;
        while ((event = eventInboxStorage.peek()).isPresent())
            process(event.get());
    }

    private void process(RawEvent event) {
        try {
            switch (event.getType()) {
                case "installation", "installation_repositories":
                    installationEventHandler.process(event.payload(RawInstallationEvent.class));
                    eventInboxStorage.ack(event.getId());
                    break;
                case "repository":
                    repositoryEventHandler.process(event.payload(RawRepositoryEvent.class));
                    eventInboxStorage.ack(event.getId());
                    break;
                default:
                    LOGGER.warn("Unknown event type: {}", event.getType());
                    eventInboxStorage.ignore(event.getId(), "Unknown event type: " + event.getType());
            }
        } catch (Exception e) {
            LOGGER.error("Error processing event: {}", event, e);
            eventInboxStorage.nack(event.getId(), e.getMessage());
        }
    }

    @Override
    public String name() {
        return "events-inbox-dequeuer";
    }
}
