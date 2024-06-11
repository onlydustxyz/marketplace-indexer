package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.NotifierJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class NewContributionNotifierJob extends Job {
    private final ContributionStorage contributionStorage;
    private final IndexingObserver indexingObserver;
    private final NotifierJobStorage notifierJobStorage;

    @Override
    public void execute() {
        LOGGER.info("Notifying upon new contributions");
        final var job = notifierJobStorage.startJob();
        try {
            final var notification =
                    contributionStorage.newContributionsNotification(Optional.ofNullable(job.getLastNotificationSentAt()).orElse(Instant.EPOCH));
            if (!Optional.ofNullable(notification.repoIds()).orElse(Set.of()).isEmpty()) {
                indexingObserver.onNewContributions(notification.repoIds());
                job.setLastNotificationSentAt(notification.latestContributionUpdate());
            }
            notifierJobStorage.endJob(job);
        } catch (Throwable e) {
            LOGGER.error("Error notifying upon new contributions", e);
            notifierJobStorage.failJob(job);
        }
    }

    @Override
    public String name() {
        return "new-contribution-notifier";
    }
}
