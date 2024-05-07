package com.onlydust.marketplace.indexer.postgres.entities;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

@Entity
@Data
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "events_inbox", schema = "indexer_raw")
@DynamicUpdate
public class EventsInboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String type;

    @JdbcTypeCode(SqlTypes.JSON)
    JsonNode payload;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "inbox_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    Status status;
    String reason;

    public EventsInboxEntity(String type, JsonNode payload) {
        this.type = type;
        this.payload = payload;
        this.status = Status.PENDING;
    }

    public EventsInboxEntity processed() {
        return this.toBuilder()
                .status(Status.PROCESSED)
                .build();
    }

    public EventsInboxEntity failed(String reason) {
        return this.toBuilder()
                .status(Status.FAILED)
                .reason(reason)
                .build();
    }

    public EventsInboxEntity ignored(String reason) {
        return this.toBuilder()
                .status(Status.IGNORED)
                .reason(reason)
                .build();
    }

    public enum Status {
        PENDING, PROCESSED, FAILED, IGNORED
    }
}
