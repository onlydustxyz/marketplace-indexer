package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.fasterxml.jackson.databind.JsonNode;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;


@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "public_events", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.public_events (data, user_id, id) VALUES (?, ?, ?) ON CONFLICT DO NOTHING")
public class RawPublicEventEntity {
    @Id
    @NonNull
    Long id;

    @NonNull
    Long actorId;

    @NonNull
    String type;

    @NonNull
    ZonedDateTime createdAt;

    @NonNull
    @JdbcTypeCode(SqlTypes.JSON)
    RawAccount actor;

    @NonNull
    @JdbcTypeCode(SqlTypes.JSON)
    RawRepo repo;

    @NonNull
    @JdbcTypeCode(SqlTypes.JSON)
    RawAccount org;

    @NonNull
    @JdbcTypeCode(SqlTypes.JSON)
    JsonNode payload;

    public static RawPublicEventEntity of(RawPublicEvent event) {
        return RawPublicEventEntity.builder()
                .id(event.id())
                .actorId(event.actor().getId())
                .type(event.type())
                .createdAt(event.createdAt())
                .actor(event.actor())
                .repo(event.repo())
                .org(event.org())
                .payload(event.payload())
                .build();
    }

    public RawPublicEvent event() {
        return new RawPublicEvent(id, type, actor, repo, org, createdAt, payload);
    }
}
