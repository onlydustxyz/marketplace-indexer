package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
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
        userIds.forEach(userId -> rawStorageReader.user(userId).ifPresentOrElse(this::index, () -> LOGGER.warn("User {} not found", userId)));
    }

    @Override
    public String name() {
        return "user-public-events-indexer";
    }

    private void index(RawAccount user) {
        try {
            userPublicEventsIndexingJobStorage.startJob(user.getId());
            final var since = userPublicEventsIndexingJobStorage.lastEventTimestamp(user.getId())
                    .orElse(ZonedDateTime.parse(user.getCreatedAt()));
            userPublicEventsIndexer.indexUser(user.getId(), since);
            userPublicEventsIndexingJobStorage.endJob(user.getId());
        } catch (Throwable e) {
            LOGGER.error("Failed to index public events for user {}", user.getId(), e);
            userPublicEventsIndexingJobStorage.failJob(user.getId());
        }
    }
}
