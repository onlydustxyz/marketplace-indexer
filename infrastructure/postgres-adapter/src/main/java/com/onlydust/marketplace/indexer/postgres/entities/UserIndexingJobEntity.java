package com.onlydust.marketplace.indexer.postgres.entities;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;

@Entity
@EqualsAndHashCode
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "user_indexing_jobs", schema = "indexer")
@TypeDef(name = "job_status", typeClass = PostgreSQLEnumType.class)
public class UserIndexingJobEntity {
    @Id
    Long userId;

    @Enumerated(EnumType.STRING)
    @Type(type = "job_status")
    JobStatus status;

    Instant startedAt;
    Instant finishedAt;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant techCreatedAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(nullable = false)
    Instant techUpdatedAt;

    public UserIndexingJobEntity(Long userId) {

        this.userId = userId;
        this.status = JobStatus.PENDING;
    }
}
