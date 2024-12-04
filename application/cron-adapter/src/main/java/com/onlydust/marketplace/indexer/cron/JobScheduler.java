package com.onlydust.marketplace.indexer.cron;

import com.onlydust.marketplace.indexer.domain.jobs.InstallationEventsInboxJob;
import com.onlydust.marketplace.indexer.domain.jobs.OtherEventsInboxJob;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
@Profile("job")
@EnableScheduling
public class JobScheduler {
    private final JobManager diffRepoRefreshJobManager;
    private final JobManager diffUserRefreshJobManager;
    private final InstallationEventsInboxJob installationEventsInboxJob;
    private final OtherEventsInboxJob otherEventsInboxJob;
    private final JobManager commitIndexerJobManager;
    private final UserPublicEventsIndexingJobManager userStatsJobManager;

    @Scheduled(fixedDelayString = "${application.cron.repo-refresh-job-delay}")
    public void scheduleRepoRefresherJobs() {
        LOGGER.debug("Refreshing repos");
        diffRepoRefreshJobManager.createJob().run();
    }

    @Scheduled(fixedDelayString = "${application.cron.user-refresh-job-delay}")
    public void scheduleUserRefresherJobs() {
        LOGGER.debug("Refreshing users");
        diffUserRefreshJobManager.createJob().run();
    }

    @Scheduled(fixedDelayString = "${application.cron.event-inbox-dequeuer-delay}")
    public void dequeueInstallationEvents() {
        installationEventsInboxJob.run();
    }

    @Scheduled(fixedDelayString = "${application.cron.event-inbox-dequeuer-delay}")
    public void dequeueOtherEvents() {
        otherEventsInboxJob.run();
    }

    @Scheduled(fixedDelayString = "${application.cron.commit-indexer-delay}")
    public void scheduleCommitIndexerJobs() {
        LOGGER.debug("Indexing commits");
        commitIndexerJobManager.createJob().run();
    }
}
