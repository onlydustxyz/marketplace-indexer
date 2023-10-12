package com.onlydust.indexer.infrastructure.quartz.adapters;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
@AllArgsConstructor
public class RepoRefresherJob implements Job {
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        final var installationId = context.getJobDetail().getJobDataMap().getLong("installationId");
        final var repos = context.getJobDetail().getJobDataMap().get("repos");
        LOGGER.info("Refreshing repos for installation {} and repos {}", installationId, repos);
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        LOGGER.info("Completed");
    }


}
