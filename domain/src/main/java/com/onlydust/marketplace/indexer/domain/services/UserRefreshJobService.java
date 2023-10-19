package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.ports.in.UserRefreshJobManager;
import com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobRepository;
import com.onlydust.marketplace.indexer.domain.ports.out.UserRefresher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class UserRefreshJobService implements UserRefreshJobManager {
    private final UserIndexingJobRepository userIndexingJobRepository;
    private final UserRefresher userRefresher;

    @Override
    public void addUserToRefresh(Long userId) {
        userIndexingJobRepository.add(userId);
    }

    @Override
    public void runAllJobs() {
        final var users = userIndexingJobRepository.users();
        userRefresher.refreshUsers(users);
    }
}
