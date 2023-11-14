package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.UserIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class UserRefreshJobService implements UserRefreshJobManager {
    private final UserIndexingJobStorage userIndexingJobStorage;
    private final UserIndexer userIndexer;

    @Override
    public void addUserToRefresh(Long userId) {
        userIndexingJobStorage.add(userId);
    }

    @Override
    public List<Job> allJobs() {
        final var users = userIndexingJobStorage.users();
        return List.of(new UserIndexerJob(userIndexer, users, userIndexingJobStorage));
    }
}
