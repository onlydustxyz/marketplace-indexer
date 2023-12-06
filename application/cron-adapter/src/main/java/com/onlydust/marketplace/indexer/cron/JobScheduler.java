package com.onlydust.marketplace.indexer.cron;

import com.onlydust.marketplace.indexer.domain.jobs.InstallationEventsInboxJob;
import com.onlydust.marketplace.indexer.domain.jobs.NewContributionNotifierJob;
import com.onlydust.marketplace.indexer.domain.jobs.OtherEventsInboxJob;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@Profile("job")
public class JobScheduler {
    private final JobManager diffRepoRefreshJobManager;
    private final JobManager diffUserRefreshJobManager;
    private final NewContributionNotifierJob newContributionNotifierJob;
    private final InstallationEventsInboxJob installationEventsInboxJob;
    private final OtherEventsInboxJob otherEventsInboxJob;

    @Scheduled(fixedDelayString = "${application.cron.repo-refresh-job-delay}")
    public void scheduleRepoRefresherJobs() {
        LOGGER.info("Refreshing repos");
        diffRepoRefreshJobManager.createJob().run();
    }

    @Scheduled(fixedDelayString = "${application.cron.user-refresh-job-delay}")
    public void scheduleUserRefresherJobs() {
        LOGGER.info("Refreshing users");
        diffUserRefreshJobManager.createJob().run();
    }

    @Scheduled(fixedDelayString = "${application.cron.api-notifier-job-delay}")
    public void notifyApi() {
        LOGGER.info("Notifying API");
        newContributionNotifierJob.run();
    }

    @Scheduled(fixedDelayString = "${application.cron.event-inbox-dequeuer-delay}")
    public void dequeueInstallationEvents() {
        installationEventsInboxJob.run();
    }

    @Scheduled(fixedDelayString = "${application.cron.event-inbox-dequeuer-delay}")
    public void dequeueOtherEvents() {
        otherEventsInboxJob.run();
    }
}
