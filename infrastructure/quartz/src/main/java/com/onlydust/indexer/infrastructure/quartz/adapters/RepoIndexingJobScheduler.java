package com.onlydust.indexer.infrastructure.quartz.adapters;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJob;
import com.onlydust.marketplace.indexer.domain.ports.out.JobScheduler;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.Map;

@AllArgsConstructor
@Slf4j
public class RepoIndexingJobScheduler implements JobScheduler<RepoIndexingJob> {
    private final Scheduler scheduler;

    private static JobKey jobKey(RepoIndexingJob job) {
        return new JobKey(job.getInstallationId().toString(), "repo_indexing_jobs");
    }

    private static TriggerKey triggerKey(RepoIndexingJob job) {
        return new TriggerKey(job.getInstallationId().toString(), "repo_indexing_trigger");
    }

    @Override
    public void scheduleJob(RepoIndexingJob job) {
        try {
            if (scheduler.checkExists(jobKey(job))) {
                LOGGER.info("Skipping indexing of installation {} as it is already running", job.getInstallationId());
                return;
            }

            LOGGER.info("Scheduling job for installation {} and repos {}", job.getInstallationId(), job.getRepos());
            scheduler.scheduleJob(newJob(job), jobTrigger(job));
        } catch (SchedulerException e) {
            LOGGER.error("Unable to schedule job for installation {}", job.getInstallationId(), e);
        }
    }

    private JobDetail newJob(RepoIndexingJob job) {
        return JobBuilder.newJob()
                .withIdentity(jobKey(job))
                .withDescription("Refresh repositories data")
                .ofType(RepoRefresherJob.class)
                .usingJobData(new JobDataMap(Map.of(
                        "installationId", job.getInstallationId(),
                        "repos", job.getRepos())))
                .build();
    }

    private Trigger jobTrigger(RepoIndexingJob job) {
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey(job))
                .withDescription("Job trigger")
                .forJob(newJob(job))
                .build();
    }
}
