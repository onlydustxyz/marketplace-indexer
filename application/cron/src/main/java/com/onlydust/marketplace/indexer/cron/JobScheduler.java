package com.onlydust.marketplace.indexer.cron;

import com.onlydust.marketplace.indexer.domain.services.RepoIndexingJobService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class JobScheduler {
    private final RepoIndexingJobService repoIndexingJobService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void scheduleRepoRefresherJobs() {
        LOGGER.info("Scheduling jobs");
        repoIndexingJobService.scheduleAllJobs();
    }
}
