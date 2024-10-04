package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPublicEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface PublicEventRepository extends JpaRepository<RawPublicEventEntity, Long> {

    List<RawPublicEventEntity> findAllByActorIdAndCreatedAtGreaterThanEqual(Long actorId, ZonedDateTime since);
}
