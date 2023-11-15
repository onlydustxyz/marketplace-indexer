package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@AllArgsConstructor
@Slf4j
public class UserIndexerJob extends Job {
    private final UserIndexer userIndexer;
    private final Set<Long> userIds;
    private final UserIndexingJobStorage userIndexingJobStorage;

    @Override
    public void execute() {
        userIds.forEach(user -> {
            try {
                LOGGER.info("Indexing users {}", userIds);
                userIndexingJobStorage.startJob(user);
                userIndexer.indexUser(user);
                userIndexingJobStorage.endJob(user);
            } catch (Throwable e) {
                LOGGER.error("Failed to index user {}", user, e);
                userIndexingJobStorage.failJob(user);
            }
        });
    }

    @Override
    public String name() {
        return "user-indexer";
    }
}
