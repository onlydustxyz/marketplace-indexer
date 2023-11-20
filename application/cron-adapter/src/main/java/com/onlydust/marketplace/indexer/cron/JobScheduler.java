package com.onlydust.marketplace.indexer.cron;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserRefreshJobManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@Slf4j
@AllArgsConstructor
@Profile("job")
public class JobScheduler {
    private final Executor applicationTaskExecutor;
    private final RepoRefreshJobManager diffRepoRefreshJobManager;
    private final UserRefreshJobManager diffUserRefreshJobManager;

    @Scheduled(fixedDelayString = "${application.cron.repo-refresh-job-delay}")
    public void scheduleRepoRefresherJobs() {
        LOGGER.info("Refreshing repos");
        diffRepoRefreshJobManager.allJobs().forEach(applicationTaskExecutor::execute);
    }

    @Scheduled(fixedDelayString = "${application.cron.user-refresh-job-delay}")
    public void scheduleUserRefresherJobs() {
        LOGGER.info("Refreshing users");
        diffUserRefreshJobManager.allJobs().forEach(applicationTaskExecutor::execute);
    }
}
