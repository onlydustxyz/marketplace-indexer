package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.util.Set;

public interface UserIndexingJobStorage {
    Set<Long> users();

    void add(Long userId);

    void startJob(Long userId);

    void failJob(Long userId);

    void endJob(Long userId);
}
