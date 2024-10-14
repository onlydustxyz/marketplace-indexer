package com.onlydust.marketplace.indexer.postgres;

import com.onlydust.marketplace.indexer.postgres.entities.EventEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import onlydust.com.marketplace.kernel.model.Event;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface OutboxRepository<E extends EventEntity> extends BaseJpaRepository<E, Long> {

    void saveEvent(Event event);

    @Query(value = """
            SELECT next_event
            FROM #{#entityName} next_event
            WHERE next_event.status = 'PENDING' OR next_event.status = 'FAILED'
            ORDER BY next_event.id ASC
            LIMIT 1
            """)
    Optional<E> findNextToProcess();
}
