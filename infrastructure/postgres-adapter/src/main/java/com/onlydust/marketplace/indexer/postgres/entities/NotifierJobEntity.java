package com.onlydust.marketplace.indexer.postgres.entities;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "notifier_jobs", schema = "indexer")
@TypeDef(name = "job_status", typeClass = PostgreSQLEnumType.class)
@DynamicUpdate
public class NotifierJobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Instant lastNotification;

    @Enumerated(EnumType.STRING)
    @Type(type = "job_status")
    JobStatus status;

    Instant startedAt;
    Instant finishedAt;
}
