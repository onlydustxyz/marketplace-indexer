package com.onlydust.marketplace.indexer.postgres.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Instant;

@Entity
@EqualsAndHashCode
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "user_indexing_jobs", schema = "indexer")
@DynamicUpdate
public class UserIndexingJobEntity {
    @Id
    Long userId;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "job_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    JobStatus status;

    Instant startedAt;
    Instant finishedAt;

    public UserIndexingJobEntity(Long userId) {

        this.userId = userId;
        this.status = JobStatus.PENDING;
    }
}
