package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserStatsIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserStatsIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.ZonedDateTime;
import java.util.Set;

@AllArgsConstructor
@Slf4j
public class UserStatsIndexerJob extends Job {
    private final UserStatsIndexer userStatsIndexer;
    private final Set<Long> userIds;
    private final UserStatsIndexingJobStorage userStatsIndexingJobStorage;
    private final RawStorageReader rawStorageReader;

    @Override
    public void execute() {
        userIds.forEach(userId -> rawStorageReader.user(userId).ifPresentOrElse(this::index, () -> LOGGER.warn("User {} not found", userId)));
    }

    @Override
    public String name() {
        return "user-stats-indexer";
    }

    private void index(RawAccount user) {
        try {
            LOGGER.info("Indexing stats for user {}", user.getId());
            userStatsIndexingJobStorage.startJob(user.getId());
            final var since = userStatsIndexingJobStorage.lastEventTimestamp(user.getId())
                    .orElse(ZonedDateTime.parse(user.getCreatedAt()));
            userStatsIndexer.indexUser(user.getId(), since);
            userStatsIndexingJobStorage.endJob(user.getId());
        } catch (Throwable e) {
            LOGGER.error("Failed to index stats for user {}", user.getId(), e);
            userStatsIndexingJobStorage.failJob(user.getId());
        }
    }
}
