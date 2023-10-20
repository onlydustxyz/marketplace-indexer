package com.onlydust.marketplace.indexer.cron;

import com.onlydust.marketplace.indexer.domain.ports.in.RepoRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.in.UserRefreshJobManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executor;

@Component
@Slf4j
@AllArgsConstructor
public class JobScheduler {
    private final Executor applicationTaskExecutor;
    private final RepoRefreshJobManager repoRefreshJobManager;
    private final UserRefreshJobManager userRefreshJobManager;

    @Scheduled(fixedDelayString = "${application.cron.repo-refresh-job-delay}")
    public void scheduleRepoRefresherJobs() {
        LOGGER.info("Refreshing repos");
        repoRefreshJobManager.allJobs().forEach(applicationTaskExecutor::execute);
    }

    @Scheduled(fixedDelayString = "${application.cron.user-refresh-job-delay}")
    public void scheduleUserRefresherJobs() {
        LOGGER.info("Refreshing users");
        userRefreshJobManager.allJobs().forEach(applicationTaskExecutor::execute);
    }
}
