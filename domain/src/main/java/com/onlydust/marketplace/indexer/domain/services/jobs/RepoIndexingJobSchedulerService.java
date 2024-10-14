package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.RepoIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@AllArgsConstructor
@Slf4j
public class RepoIndexingJobSchedulerService implements RepoIndexingJobScheduler {
    private final RepoIndexingJobStorage repoIndexingJobStorage;

    @Override
    public void addReposToRefresh(Set<Long> repoIds) {
        repoIndexingJobStorage.configureReposForFullIndexing(repoIds, true);
    }

    @Override
    public void removeReposToRefresh(Set<Long> repoIds) {
        repoIndexingJobStorage.configureRepoForLightIndexing(repoIds);
    }
}
