package com.onlydust.marketplace.indexer.domain.jobs;

import com.onlydust.marketplace.indexer.domain.models.CommitIndexingJobItem;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.CommitIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.CommitIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class CommitIndexerJob extends Job {
    private final CommitIndexer commitIndexer;
    private final CommitIndexingJobStorage commitIndexingJobStorage;
    private final RateLimitService rateLimitService;

    @Override
    public void execute() {
        final var batchSize = rateLimitService.rateLimit().remaining() - 1000;

        if (batchSize <= 0) {
            LOGGER.info("Rate limit exceeded, waiting for reset");
            return;
        }

        LOGGER.info("Indexing {} commits", batchSize);
        commitIndexingJobStorage.commitsForLeastIndexedUsers(batchSize)
                .forEach(this::index);
    }

    private void index(final @NonNull CommitIndexingJobItem item) {
        try {
            commitIndexer.indexCommit(item.repoId(), item.sha());
        } catch (Throwable e) {
            LOGGER.warn("Failed to index commit {}/{} ", item.repoId(), item.sha(), e);
        }
    }

    @Override
    public String name() {
        return "commit-indexer";
    }
}
