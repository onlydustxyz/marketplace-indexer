package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.jobs.InstallationEventsInboxJob;
import com.onlydust.marketplace.indexer.domain.jobs.OtherEventsInboxJob;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobsConfiguration {
    @Bean
    public OtherEventsInboxJob otherEventsInboxJob(final EventInboxStorage eventInboxStorage,
                                                   final EventHandler<RawRepositoryEvent> repositoryEventHandler,
                                                   final EventHandler<RawStarEvent> starEventHandler,
                                                   final EventHandler<RawIssueEvent> issueEventHandler,
                                                   final EventHandler<RawPullRequestEvent> pullRequestEventHandler) {
        return new OtherEventsInboxJob(eventInboxStorage, repositoryEventHandler, starEventHandler, issueEventHandler, pullRequestEventHandler);
    }

    @Bean
    public InstallationEventsInboxJob installationEventsInboxJob(final EventInboxStorage eventInboxStorage,
                                                                 final EventHandler<RawInstallationEvent> installationEventHandler) {
        return new InstallationEventsInboxJob(eventInboxStorage, installationEventHandler);
    }
}
