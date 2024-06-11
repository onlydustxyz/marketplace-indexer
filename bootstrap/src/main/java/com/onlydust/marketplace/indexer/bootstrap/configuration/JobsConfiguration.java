package com.onlydust.marketplace.indexer.bootstrap.configuration;

import com.onlydust.marketplace.indexer.domain.jobs.InstallationEventsInboxJob;
import com.onlydust.marketplace.indexer.domain.jobs.NewContributionNotifierJob;
import com.onlydust.marketplace.indexer.domain.jobs.OtherEventsInboxJob;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.NotifierJobStorage;
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

    @Bean
    public NewContributionNotifierJob newContributionNotifierJob(
            final ContributionStorage contributionStorage,
            final IndexingObserver indexingObserver,
            final NotifierJobStorage notifierJobStorage
    ) {
        return new NewContributionNotifierJob(contributionStorage, indexingObserver, notifierJobStorage);
    }
}
