package com.onlydust.indexer.infrastructure.quartz.adapters;

import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.HashSet;

@Slf4j
@AllArgsConstructor
public class RepoRefresherJob implements Job {
    private final IndexingService indexingService;

    @Override
    public void execute(JobExecutionContext context) {
        final var installationId = context.getJobDetail().getJobDataMap().getLong("installationId");
        final var repos = context.getJobDetail().getJobDataMap().get("repos");
        if (repos instanceof HashSet) {
            ((HashSet<?>) repos).forEach(repo -> indexingService.indexRepo((Long) repo));
        }
    }
}
