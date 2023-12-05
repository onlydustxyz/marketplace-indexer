package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.jobs.EventsInboxJob;
import com.onlydust.marketplace.indexer.domain.jobs.NewContributionNotifierJob;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.ApiClient;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.NotifierJobStorage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobsConfiguration {
    @Bean
    public EventsInboxJob eventsInboxJob(final EventInboxStorage eventInboxStorage,
                                         final EventHandler<RawInstallationEvent> installationEventHandler,
                                         final EventHandler<RawRepositoryEvent> repositoryEventHandler,
                                         final EventHandler<RawStarEvent> starEventHandler,
                                         final EventHandler<RawIssueEvent> issueEventHandler,
                                         final EventHandler<RawPullRequestEvent> pullRequestEventHandler) {
        return new EventsInboxJob(eventInboxStorage, installationEventHandler, repositoryEventHandler, starEventHandler, issueEventHandler, pullRequestEventHandler);
    }

    @Bean
    public NewContributionNotifierJob newContributionNotifierJob(
            final ContributionStorage contributionStorage,
            final ApiClient apiClient,
            final NotifierJobStorage notifierJobStorage
    ) {
        return new NewContributionNotifierJob(contributionStorage, apiClient, notifierJobStorage);
    }
}
