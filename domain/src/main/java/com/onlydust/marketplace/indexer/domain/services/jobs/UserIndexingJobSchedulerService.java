package com.onlydust.marketplace.indexer.domain.services.jobs;

import com.onlydust.marketplace.indexer.domain.ports.in.jobs.UserIndexingJobScheduler;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class UserIndexingJobSchedulerService implements UserIndexingJobScheduler {
    private final UserIndexingJobStorage userIndexingJobStorage;

    @Override
    public void addUserToRefresh(Long userId) {
        userIndexingJobStorage.add(userId);
    }
}
