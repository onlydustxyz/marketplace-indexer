package com.onlydust.indexer.infrastructure.quartz.adapters;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJob;
import com.onlydust.marketplace.indexer.domain.ports.out.JobScheduler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Map;

@AllArgsConstructor
@Slf4j
public class UserIndexingJobScheduler implements JobScheduler<UserIndexingJob> {
    private final Scheduler scheduler;

    private static JobKey jobKey(UserIndexingJob job) {
        return new JobKey("user_indexing_jobs");
    }

    private static TriggerKey triggerKey(UserIndexingJob job) {
        return new TriggerKey("user_indexing_trigger");
    }

    @Override
    public void scheduleJob(UserIndexingJob job) {
        try {
            if (scheduler.checkExists(jobKey(job))) {
                LOGGER.info("Skipping indexing of users as it is already running");
                return;
            }

            LOGGER.info("Scheduling job for users {}", job.getUsers());
            scheduler.scheduleJob(newJob(job), jobTrigger(job));
        } catch (SchedulerException e) {
            LOGGER.error("Unable to schedule job for users", e);
        }
    }

    private JobDetail newJob(UserIndexingJob job) {
        return JobBuilder.newJob()
                .withIdentity(jobKey(job))
                .withDescription("Refresh users data")
                .ofType(UserRefresherJob.class)
                .usingJobData(new JobDataMap(Map.of(
                        "users", job.getUsers())))
                .build();
    }

    private Trigger jobTrigger(UserIndexingJob job) {
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey(job))
                .withDescription("User refresh Job trigger")
                .forJob(newJob(job))
                .build();
    }
}
