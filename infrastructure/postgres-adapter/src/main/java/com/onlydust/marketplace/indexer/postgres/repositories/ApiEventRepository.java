package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.OutboxRepository;
import com.onlydust.marketplace.indexer.postgres.entities.ApiEventEntity;
import onlydust.com.marketplace.kernel.model.Event;

public interface ApiEventRepository extends OutboxRepository<ApiEventEntity> {

    @Override
    default void saveEvent(Event event) {
        save(new ApiEventEntity(event));
    }

}
