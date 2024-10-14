package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPublicEventEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface PublicEventRepository extends BaseJpaRepository<RawPublicEventEntity, Long> {

    List<RawPublicEventEntity> findAllByActorIdAndCreatedAtGreaterThanEqual(Long actorId, ZonedDateTime since);
}
