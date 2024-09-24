package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.UserStatsIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserStatsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserStatsIndexingJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserStatsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@AllArgsConstructor
@Slf4j
public class UserStatsIndexingJobService implements UserStatsIndexingJobManager {
    private final UserStatsIndexingJobStorage userStatsIndexingJobStorage;
    private final UserStatsIndexer userStatsIndexer;
    private final RawStorageReader rawStorageReader;
    private final Config config;

    @Override
    public Job create(Long userId) {
        userStatsIndexingJobStorage.add(userId);
        return new UserStatsIndexerJob(userStatsIndexer, Set.of(userId), userStatsIndexingJobStorage, rawStorageReader);
    }

    @Override
    public Job refresh() {
        final var users = userStatsIndexingJobStorage.all();
        return new UserStatsIndexerJob(userStatsIndexer, users, userStatsIndexingJobStorage, rawStorageReader);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {
        Integer refreshInterval;
    }
}
