package com.onlydust.marketplace.indexer.cron;

import com.onlydust.marketplace.indexer.domain.ports.in.RefreshJobScheduler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class JobScheduler {
    private final RefreshJobScheduler repoRefreshJobScheduler;
    private final RefreshJobScheduler userRefreshJobScheduler;

    @Scheduled(fixedDelayString = "${application.cron.repo-refresh-job-delay}")
    public void scheduleRepoRefresherJobs() {
        LOGGER.info("Scheduling repo refresh jobs");
        repoRefreshJobScheduler.scheduleAllJobs();
    }

    @Scheduled(fixedDelayString = "${application.cron.user-refresh-job-delay}")
    public void scheduleUserRefresherJobs() {
        LOGGER.info("Scheduling user refresh jobs");
        userRefreshJobScheduler.scheduleAllJobs();
    }
}