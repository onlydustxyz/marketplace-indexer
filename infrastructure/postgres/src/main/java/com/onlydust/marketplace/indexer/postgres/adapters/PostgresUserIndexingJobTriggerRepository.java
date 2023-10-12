package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobTriggerEntityRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PostgresUserIndexingJobTriggerRepository implements com.onlydust.marketplace.indexer.domain.ports.out.UserIndexingJobTriggerRepository {
    private final UserIndexingJobTriggerEntityRepository userIndexingJobTriggerRepository;

    @Override
    public List<UserIndexingJobTrigger> list() {
        return userIndexingJobTriggerRepository.findAll().stream()
                .map(trigger -> new UserIndexingJobTrigger(trigger.getUserId()))
                .toList();
    }
}
