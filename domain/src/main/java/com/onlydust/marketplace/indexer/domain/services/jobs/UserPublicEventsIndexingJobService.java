package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.UserStatsIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@AllArgsConstructor
@Slf4j
public class UserPublicEventsIndexingJobService implements UserPublicEventsIndexingJobManager {
    private final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage;
    private final UserPublicEventsIndexer userPublicEventsIndexer;
    private final RawStorageReader rawStorageReader;
    private final Config config;

    @Override
    public Job create(Long userId) {
        userPublicEventsIndexingJobStorage.add(userId);
        return new UserStatsIndexerJob(userPublicEventsIndexer, Set.of(userId), userPublicEventsIndexingJobStorage, rawStorageReader);
    }

    @Override
    public Job refresh() {
        final var users = userPublicEventsIndexingJobStorage.all();
        return new UserStatsIndexerJob(userPublicEventsIndexer, users, userPublicEventsIndexingJobStorage, rawStorageReader);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Config {
        Integer refreshInterval;
    }
}
