package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import java.time.Instant;
import java.util.Set;

public interface UserIndexingJobStorage {
    Set<Long> usersUpdatedBefore(Instant since);

    void add(Long userId);

    void startJob(Long userId);

    void failJob(Long userId);

    void endJob(Long userId);
}
