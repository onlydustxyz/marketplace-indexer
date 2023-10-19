package com.onlydust.indexer.infrastructure.quartz.adapters;

import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.UserRefresher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class QuartzUserRefresherJob implements UserRefresher, Job {
    final static JobKey JOB_KEY = new JobKey("user_indexing_jobs");
    final static TriggerKey TRIGGER_KEY = new TriggerKey("user_indexing_trigger");

    private final Scheduler scheduler;
    private final UserIndexer userIndexer;

    @Override
    public void refreshUsers(Set<Long> userIds) {
        try {
            if (scheduler.checkExists(JOB_KEY)) {
                LOGGER.info("Skipping indexing of users as it is already running");
                return;
            }

            LOGGER.info("Scheduling job for users {}", userIds);
            final var job = newJob(userIds);
            final var trigger = jobTrigger(job);
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOGGER.error("Unable to schedule job for users", e);
        }
    }

    private JobDetail newJob(Set<Long> userIds) {
        return JobBuilder.newJob()
                .withIdentity(JOB_KEY)
                .withDescription("Refresh users data")
                .ofType(getClass())
                .usingJobData(new JobDataMap(Map.of("users", userIds)))
                .build();
    }

    private Trigger jobTrigger(JobDetail job) {
        return TriggerBuilder.newTrigger()
                .withIdentity(TRIGGER_KEY)
                .withDescription("User refresh Job trigger")
                .forJob(job)
                .build();
    }

    @Override
    public void execute(JobExecutionContext context) {
        final var users = context.getJobDetail().getJobDataMap().get("users");
        if (users instanceof HashSet) {
            ((HashSet<?>) users).forEach(user -> userIndexer.indexUser((Long) user));
        }
    }
}
