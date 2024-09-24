package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.models.raw.RawGithubAppEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class InstallationEventsInboxJob extends Job {
    private final EventInboxStorage eventInboxStorage;
    private final EventHandler<RawInstallationEvent> installationEventHandler;

    @Override
    protected void execute() {
        Optional<RawGithubAppEvent> event;
        while ((event = eventInboxStorage.peek("installation", "installation_repositories")).isPresent())
            process(event.get());
    }

    @Retryable(maxAttempts = 6, backoff = @Backoff(delay = 500, multiplier = 2))
    private void process(RawGithubAppEvent event) {
        try {
            installationEventHandler.process(event.payload(RawInstallationEvent.class));
            eventInboxStorage.ack(event.id());
        } catch (Exception e) {
            LOGGER.error("Error processing event: {}", event.toString(), e);
            eventInboxStorage.nack(event.id(), e.getMessage() == null ? e.toString() : e.getMessage());
        }
    }

    @Override
    public String name() {
        return "installation-events-inbox-dequeuer";
    }
}
