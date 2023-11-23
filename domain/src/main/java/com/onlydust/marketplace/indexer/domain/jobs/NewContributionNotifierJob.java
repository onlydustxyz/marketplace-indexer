package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.ports.out.ApiClient;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.NotifierJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class NewContributionNotifierJob extends Job {
    private final ContributionStorage contributionStorage;
    private final ApiClient apiClient;
    private final NotifierJobStorage notifierJobStorage;

    @Override
    public void execute() {
        LOGGER.info("Notifying upon new contributions");
        final var job = notifierJobStorage.startJob();
        try {
            final var notification = contributionStorage.newContributionsNotification(Optional.ofNullable(job.getLastNotificationSentAt()).orElse(Instant.EPOCH));
            if (!notification.repoIds().isEmpty()) {
                apiClient.onNewContributions(notification.repoIds());
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
