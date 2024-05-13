package com.onlydust.marketplace.indexer.postgres.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Instant;

@Entity
@Value
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "notifier_jobs", schema = "indexer")
@DynamicUpdate
public class NotifierJobEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    Instant lastNotification;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "job_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    JobStatus status;

    Instant startedAt;
    Instant finishedAt;
}
