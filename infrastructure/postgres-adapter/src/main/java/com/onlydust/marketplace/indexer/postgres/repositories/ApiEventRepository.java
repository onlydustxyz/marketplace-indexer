package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.OutboxRepository;
import com.onlydust.marketplace.indexer.postgres.entities.ApiEventEntity;
import onlydust.com.marketplace.kernel.model.Event;

import java.util.List;

public interface ApiEventRepository extends OutboxRepository<ApiEventEntity> {

    @Override
    default void saveEvent(Event event) {
        persist(new ApiEventEntity(event));
    }

    void deleteAll();

    List<ApiEventEntity> findAll();
}
