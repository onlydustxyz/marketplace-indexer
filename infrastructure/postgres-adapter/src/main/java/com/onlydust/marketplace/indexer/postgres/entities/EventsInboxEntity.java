package com.onlydust.marketplace.indexer.postgres.entities;

import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;
import org.hibernate.type.SqlTypes;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "events_inbox", schema = "indexer_raw")
@DynamicUpdate
@Getter
@Setter
@Accessors(chain = true, fluent = true)
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
        this.status(Status.PROCESSED);
        return this;
    }

    public EventsInboxEntity failed(String reason) {
        this.status(Status.FAILED)
                .reason(reason);
        return this;
    }

    public EventsInboxEntity ignored(String reason) {
        this.status(Status.IGNORED)
                .reason(reason);
        return this;
    }

    public enum Status {
        PENDING, PROCESSED, FAILED, IGNORED
    }
}
