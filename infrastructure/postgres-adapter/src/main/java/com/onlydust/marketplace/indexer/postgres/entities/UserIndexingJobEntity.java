package com.onlydust.marketplace.indexer.postgres.entities;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Setter
@Accessors(fluent = true, chain = true)
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
