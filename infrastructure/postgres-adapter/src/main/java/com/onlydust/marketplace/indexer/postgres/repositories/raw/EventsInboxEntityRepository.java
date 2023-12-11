package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.EventsInboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EventsInboxEntityRepository extends JpaRepository<EventsInboxEntity, Long> {
    @Query(value = """
            SELECT id, type, payload, status, reason 
            FROM indexer_raw.events_inbox i
            WHERE status != 'PROCESSED' AND 
            type IN (:types) AND
            NOT EXISTS (
                SELECT 1 FROM indexer_raw.events_inbox i2  
                WHERE i2.status = 'FAILED' AND i.installation_id = i2.installation_id
            ) 
            ORDER BY tech_created_at 
            LIMIT 1
            """, nativeQuery = true)
    Optional<EventsInboxEntity> findFirstToProcess(final List<String> types);
}
