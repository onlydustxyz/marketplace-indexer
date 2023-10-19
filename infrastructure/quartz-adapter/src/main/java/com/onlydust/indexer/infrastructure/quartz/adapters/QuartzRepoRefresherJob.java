package com.onlydust.indexer.infrastructure.quartz.adapters;

import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoRefresher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class QuartzRepoRefresherJob implements RepoRefresher, Job {
    private final Scheduler scheduler;
    private final RepoIndexer repoIndexer;

    private static JobKey jobKey(Long installationId) {
        return new JobKey(installationId.toString(), "repo_indexing_jobs");
    }

    private static TriggerKey triggerKey(Long installationId) {
        return new TriggerKey(installationId.toString(), "repo_indexing_trigger");
    }

    @Override
    public void refreshRepos(Long installationId, Set<Long> repoIds) {
        try {
            if (scheduler.checkExists(jobKey(installationId))) {
                LOGGER.info("Skipping indexing of installation {} as it is already running", installationId);
                return;
            }

            LOGGER.info("Scheduling job for installation {} and repos {}", installationId, repoIds);
            final var job = newJob(installationId, repoIds);
            final var trigger = jobTrigger(job, installationId);
            scheduler.scheduleJob(job, trigger);
        } catch (SchedulerException e) {
            LOGGER.error("Unable to schedule job for installation {}", installationId, e);
        }
    }

    private JobDetail newJob(Long installationId, Set<Long> repoIds) {
        return JobBuilder.newJob()
                .withIdentity(jobKey(installationId))
                .withDescription("Refresh repositories data")
                .ofType(getClass())
                .usingJobData(new JobDataMap(Map.of(
                        "installationId", installationId,
                        "repos", repoIds)))
                .build();
    }

    private Trigger jobTrigger(JobDetail job, Long installationId) {
        return TriggerBuilder.newTrigger()
                .withIdentity(triggerKey(installationId))
                .withDescription("Repo refresh Job trigger")
                .forJob(job)
                .build();
    }

    @Override
    public void execute(JobExecutionContext context) {
        final var installationId = context.getJobDetail().getJobDataMap().getLong("installationId");
        final var repos = context.getJobDetail().getJobDataMap().get("repos");
        if (repos instanceof HashSet) {
            ((HashSet<?>) repos).forEach(repo -> repoIndexer.indexRepo((Long) repo));
        }
    }
}
