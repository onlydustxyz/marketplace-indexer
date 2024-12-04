package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.UserPublicEventIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class UserPublicEventsIndexingJobService implements UserPublicEventsIndexingJobManager {
    private final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage;
    private final UserPublicEventsIndexer userPublicEventsIndexer;
    private final RawStorageReader rawStorageReader;

    @Override
    public Job create(Long userId) {
        userPublicEventsIndexingJobStorage.add(userId);
        return job(userId);
    }

    @Override
    public String name(Long userId) {
        return job(userId).name();
    }

    private Job job(Long userId) {
        return new UserPublicEventIndexerJob(userPublicEventsIndexer, userId, userPublicEventsIndexingJobStorage, rawStorageReader);
    }
}
