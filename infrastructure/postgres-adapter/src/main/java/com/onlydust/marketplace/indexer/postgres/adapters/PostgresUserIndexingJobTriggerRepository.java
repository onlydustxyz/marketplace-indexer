package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobTriggerRepository;
import com.onlydust.marketplace.indexer.postgres.entities.UserIndexingJobTriggerEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobTriggerEntityRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PostgresUserIndexingJobTriggerRepository implements UserIndexingJobTriggerRepository {
    private final UserIndexingJobTriggerEntityRepository userIndexingJobTriggerRepository;

    @Override
    public void add(UserIndexingJobTrigger trigger) {
        userIndexingJobTriggerRepository.save(UserIndexingJobTriggerEntity.of(trigger));
    }

    @Override
    public List<UserIndexingJobTrigger> list() {
        return userIndexingJobTriggerRepository.findAll().stream()
                .map(trigger -> new UserIndexingJobTrigger(trigger.getUserId()))
                .toList();
    }
}
