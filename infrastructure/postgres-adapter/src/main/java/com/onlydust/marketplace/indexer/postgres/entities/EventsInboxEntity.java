package com.onlydust.marketplace.indexer.postgres.entities;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;

@Entity
@Data
@Builder(toBuilder = true, access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name = "events_inbox", schema = "indexer_raw")
@TypeDef(name = "inbox_status", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@DynamicUpdate
public class EventsInboxEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    String type;

    @Type(type = "jsonb")
    byte[] payload;
    @Enumerated(EnumType.STRING)
    @Type(type = "inbox_status")
    Status status;
    String reason;

    public EventsInboxEntity(String type, byte[] payload) {
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

    private enum Status {
        PENDING, PROCESSED, FAILED, IGNORED
    }
}
