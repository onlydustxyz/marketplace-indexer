package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.CommitIndexerJob;
import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.CommitIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.CommitIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class CommitIndexerJobService implements JobManager {
    private final CommitIndexer commitIndexer;
    private final CommitIndexingJobStorage commitIndexingJobStorage;
    private final RateLimitService rateLimitService;

    @Override
    public Job createJob() {
        return new CommitIndexerJob(commitIndexer, commitIndexingJobStorage, rateLimitService);
    }
}
