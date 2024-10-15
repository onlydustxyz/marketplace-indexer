package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.CommitIndexerJob;
import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.CommitIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.JobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.UserFileExtensionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.CommitIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class CommitRefreshJobService implements JobManager {
    private final CommitIndexingJobStorage commitIndexingJobStorage;
    private final CommitIndexer commitIndexer;
    private final UserFileExtensionStorage userFileExtensionStorage;

    @Override
    public Job createJob() {
        userFileExtensionStorage.clear();
        final var items = commitIndexingJobStorage.all();
        return new CommitIndexerJob(commitIndexer, items);
    }
}
