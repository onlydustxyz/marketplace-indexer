package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.UserIndexingJobTriggerEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobTriggerEntityRepository;
import lombok.AllArgsConstructor;

import java.util.Set;

import static java.util.stream.Collectors.toUnmodifiableSet;

@AllArgsConstructor
public class PostgresUserIndexingJobStorage implements UserIndexingJobStorage {
    private final UserIndexingJobTriggerEntityRepository userIndexingJobTriggerRepository;

    @Override
    public Set<Long> users() {
        return userIndexingJobTriggerRepository.findAll().stream()
                .map(UserIndexingJobTriggerEntity::getUserId)
                .collect(toUnmodifiableSet());
    }

    @Override
    public void add(Long userId) {
        userIndexingJobTriggerRepository.save(new UserIndexingJobTriggerEntity(userId));
    }
}
