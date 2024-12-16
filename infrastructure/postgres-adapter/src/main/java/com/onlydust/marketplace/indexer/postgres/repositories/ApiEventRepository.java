package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.ApiEventEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import lombok.NonNull;
import onlydust.com.marketplace.kernel.infrastructure.postgres.OutboxRepository;
import onlydust.com.marketplace.kernel.model.Event;

import java.util.List;

public interface ApiEventRepository extends OutboxRepository<ApiEventEntity>, BaseJpaRepository<ApiEventEntity, Long> {

    @Override
    default void saveEvent(@NonNull Event event) {
        persist(new ApiEventEntity(event));
    }

    void deleteAll();

    List<ApiEventEntity> findAll();
}
