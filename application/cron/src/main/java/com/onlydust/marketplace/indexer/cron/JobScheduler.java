package com.onlydust.marketplace.indexer.cron;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class JobScheduler {
    private final Scheduler scheduler;

    @Scheduled(cron = "0/5 * * * * ?")
    public void scheduleRepoRefresherJobs() throws SchedulerException {
        LOGGER.info("Scheduling task");
        if (scheduler.checkExists(new JobKey("repo_refresher_job"))) {
            LOGGER.info("Not triggering job as already executing");
        } else {
            scheduler.scheduleJob(newJob(), jobTrigger());
        }
    }

    private JobDetail newJob() {
        return JobBuilder.newJob()
                .withIdentity("repo_refresher_job")
                .withDescription("Refresh repositories data")
                .ofType(RepoRefresherJob.class)
                .usingJobData("installationId", 123456L)
                .build();
    }

    private Trigger jobTrigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity("job_trigger")
                .withDescription("Job trigger")
                .forJob(newJob())
                .build();
    }
}
