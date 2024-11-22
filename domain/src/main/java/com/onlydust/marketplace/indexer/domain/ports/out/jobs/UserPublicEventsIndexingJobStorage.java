package com.onlydust.marketplace.indexer.domain.ports.out.jobs;

import lombok.NonNull;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

public interface UserPublicEventsIndexingJobStorage {
    void add(Long userId);

    void startJob(Long userId);

    void failJob(Long userId);

    void endJob(Long userId);

    Optional<ZonedDateTime> lastEventTimestamp(Set<Long> userIds);

    Set<Long> all();

    void saveLastEventTimestamp(Long userId, @NonNull ZonedDateTime timestamp);
}
