package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.UserPublicEventIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserPublicEventsIndexingJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@AllArgsConstructor
@Slf4j
public class UserPublicEventsIndexingJobService implements UserPublicEventsIndexingJobManager {
    private final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage;
    private final UserPublicEventsIndexer userPublicEventsIndexer;
    private final RawStorageReader rawStorageReader;

    @Override
    public Job create(Long userId) {
        userPublicEventsIndexingJobStorage.add(userId);
        return new UserPublicEventIndexerJob(userPublicEventsIndexer, Set.of(userId), userPublicEventsIndexingJobStorage, rawStorageReader);
    }

    @Override
    public Job refresh() {
        final var users = userPublicEventsIndexingJobStorage.all();
        return new UserPublicEventIndexerJob(userPublicEventsIndexer, users, userPublicEventsIndexingJobStorage, rawStorageReader);
    }

    @Override
    public String name() {
        return "user_public_event_indexer";
    }
}
