package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.jobs.EventsInboxJob;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobsConfiguration {
    @Bean
    public EventsInboxJob eventsInboxJob(EventInboxStorage eventInboxStorage,
                                         EventHandler<RawInstallationEvent> installationEventHandler,
                                         EventHandler<RawRepositoryEvent> repositoryEventHandler) {
        return new EventsInboxJob(eventInboxStorage, installationEventHandler, repositoryEventHandler);
    }
}
