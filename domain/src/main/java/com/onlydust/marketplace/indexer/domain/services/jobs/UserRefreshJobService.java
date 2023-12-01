package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.UserIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@AllArgsConstructor
@Slf4j
public class UserRefreshJobService implements JobManager {
    private final UserIndexingJobStorage userIndexingJobStorage;
    private final UserIndexer userIndexer;
    private final Config config;

    @Override
    public Job createJob() {
        final var users = userIndexingJobStorage.usersUpdatedBefore(Instant.now().minusSeconds(config.refreshInterval));
        return new UserIndexerJob(userIndexer, users, userIndexingJobStorage);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {
        Integer refreshInterval;
    }
}
