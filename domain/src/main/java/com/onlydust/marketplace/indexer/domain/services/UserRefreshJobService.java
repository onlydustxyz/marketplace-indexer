package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.jobs.UserIndexerJob;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.UserRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@AllArgsConstructor
@Slf4j
public class UserRefreshJobService implements UserRefreshJobManager {
    private final UserIndexingJobRepository userIndexingJobRepository;
    private final UserIndexer userIndexer;

    @Override
    public void addUserToRefresh(Long userId) {
        userIndexingJobRepository.add(userId);
    }

    @Override
    public List<Job> allJobs() {
        final var users = userIndexingJobRepository.users();
        return List.of(new UserIndexerJob(userIndexer, users));
    }
}
