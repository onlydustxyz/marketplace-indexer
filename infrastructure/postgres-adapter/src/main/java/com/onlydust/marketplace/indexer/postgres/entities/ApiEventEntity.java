package com.onlydust.marketplace.indexer.postgres.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import onlydust.com.marketplace.kernel.model.Event;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@Table(name = "api_events", schema = "indexer_outbox")
@EntityListeners(AuditingEntityListener.class)
public class ApiEventEntity extends EventEntity {

    public ApiEventEntity(Event event) {
        super(event);
    }
}
