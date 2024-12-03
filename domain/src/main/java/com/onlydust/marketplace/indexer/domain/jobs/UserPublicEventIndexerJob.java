package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserPublicEventsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class UserPublicEventIndexerJob extends Job {
    private final UserPublicEventsIndexer userPublicEventsIndexer;
    private final Set<Long> userIds;
    private final UserPublicEventsIndexingJobStorage userPublicEventsIndexingJobStorage;
    private final RawStorageReader rawStorageReader;

    @Override
    public void execute() {
        try {
            start();
            final var since = userPublicEventsIndexingJobStorage.lastEventTimestamp(userIds);

            if (userIds.size() == 1) {
                final var userId = userIds.iterator().next();
                rawStorageReader.user(userId)
                        .ifPresentOrElse(user -> userPublicEventsIndexer.indexUser(user.getId(), since.orElse(ZonedDateTime.parse(user.getCreatedAt()))),
                                () -> LOGGER.warn("User {} not found", userId));
            } else {
                since.ifPresentOrElse(s -> userPublicEventsIndexer.indexUsers(userIds, s),
                        () -> LOGGER.warn("No last event timestamp found for users: {}", userIds));
            }

            end();
        } catch (Throwable e) {
            fail(e);
            throw e;
        }
    }

    private void end() {
        userIds.forEach(userPublicEventsIndexingJobStorage::endJob);
    }

    private void start() {
        userIds.forEach(userPublicEventsIndexingJobStorage::startJob);
    }

    private void fail(Throwable e) {
        LOGGER.error("Failed to index public events for users: {}", userIds, e);
        userIds.forEach(userPublicEventsIndexingJobStorage::failJob);
    }

    @Override
    public String name() {
        return "user-public-events-indexer-" + (userIds.size() == 1 ? userIds.iterator().next() : "many");
    }
}
