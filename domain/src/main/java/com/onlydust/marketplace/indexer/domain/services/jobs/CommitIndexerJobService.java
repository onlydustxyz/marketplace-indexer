package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.CommitIndexerJob;
import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.CommitIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.CommitIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static java.lang.Math.max;

@AllArgsConstructor
@Slf4j
public class CommitIndexerJobService implements JobManager {
    private final CommitIndexer commitIndexer;
    private final CommitIndexingJobStorage commitIndexingJobStorage;
    private final RateLimitService rateLimitService;

    @Override
    public Job createJob() {
        final var batchSize = max(0, rateLimitService.rateLimit().remaining() - 1000);

        LOGGER.info("Indexing max {} commits", batchSize);
        final var items = commitIndexingJobStorage.commitsForLeastIndexedUsers(batchSize);
        return new CommitIndexerJob(commitIndexer, items);
    }
}
