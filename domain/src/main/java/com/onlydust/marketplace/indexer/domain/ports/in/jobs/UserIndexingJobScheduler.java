package com.onlydust.marketplace.indexer.domain.ports.in.jobs;

public interface UserIndexingJobScheduler {
    void addUserToRefresh(Long userId);
}
