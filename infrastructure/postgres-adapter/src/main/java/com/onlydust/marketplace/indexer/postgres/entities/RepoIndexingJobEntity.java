package com.onlydust.marketplace.indexer.postgres.entities;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.*;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@EqualsAndHashCode
@Builder(toBuilder = true)
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "repo_indexing_jobs", schema = "indexer")
@TypeDef(name = "job_status", typeClass = PostgreSQLEnumType.class)
@DynamicUpdate
public class RepoIndexingJobEntity {
    @Id
    Long repoId;

    Long installationId;

    ZonedDateTime suspendedAt;

    @Enumerated(EnumType.STRING)
    @Type(type = "job_status")
    JobStatus status;

    ZonedDateTime startedAt;
    ZonedDateTime finishedAt;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    ZonedDateTime techCreatedAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(nullable = false)
    ZonedDateTime techUpdatedAt;

    public RepoIndexingJobEntity(Long repoId, Long installationId) {
        this.repoId = repoId;
        this.installationId = installationId;
        this.status = JobStatus.PENDING;
    }

}
