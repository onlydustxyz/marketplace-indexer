package com.onlydust.indexer.infrastructure.quartz.adapters;

import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.HashSet;

@Slf4j
public class UserRefresherJob implements Job {
    private final IndexingService indexingService;

    public UserRefresherJob(@Qualifier("refreshingIndexer") final IndexingService indexingService) {
        this.indexingService = indexingService;
    }

    @Override
    public void execute(JobExecutionContext context) {
        final var users = context.getJobDetail().getJobDataMap().get("users");
        if (users instanceof HashSet) {
            ((HashSet<?>) users).forEach(user -> indexingService.indexUser((Long) user));
        }
    }
}
