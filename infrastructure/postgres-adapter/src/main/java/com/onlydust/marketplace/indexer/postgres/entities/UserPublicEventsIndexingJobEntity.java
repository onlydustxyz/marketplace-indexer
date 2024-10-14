package com.onlydust.marketplace.indexer.postgres.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Instant;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_public_events_indexing_jobs", schema = "indexer")
@DynamicUpdate
@NoArgsConstructor(force = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Builder
@Getter
@Setter
@Accessors(fluent = true, chain = true)
@ToString
public class UserPublicEventsIndexingJobEntity {
    @Id
    @NonNull
    final Long userId;

    @NonNull
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "job_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Builder.Default
    JobStatus status = JobStatus.PENDING;

    @Builder.Default
    Instant startedAt = null;

    @Builder.Default
    Instant finishedAt = null;

    @Builder.Default
    ZonedDateTime lastEventTimestamp = null;
}
