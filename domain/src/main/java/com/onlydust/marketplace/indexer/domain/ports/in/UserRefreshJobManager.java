package com.onlydust.marketplace.indexer.domain.ports.in;

public interface UserRefreshJobManager {
    void addUserToRefresh(Long userId);

    void runAllJobs();
}
