package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.EventsInboxEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventsInboxEntityRepository extends BaseJpaRepository<EventsInboxEntity, Long> {
    @Query(value = """
            SELECT id, type, payload, status, reason
            FROM indexer_raw.events_inbox i
            WHERE status = 'PENDING' AND type = any (:types)
            ORDER BY tech_created_at
            LIMIT 1
            """, nativeQuery = true)
    Optional<EventsInboxEntity> findFirstToProcess(final String[] types);

    List<EventsInboxEntity> findAllByType(String type);

    List<EventsInboxEntity> findAll();
}
