package com.onlydust.marketplace.indexer.postgres.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.time.Instant;
import java.util.Date;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "repo_indexing_jobs", schema = "indexer")
@Getter
@DynamicUpdate
public class RepoIndexingJobEntity {
    @Id
    Long repoId;
    Long installationId;
    Date installationSuspendedAt;
    Boolean fullIndexing;
    Boolean isPublic;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "job_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    JobStatus status;

    Instant startedAt;
    Instant finishedAt;

    public RepoIndexingJobEntity(Long repoId, Long installationId, Boolean fullIndexing, Boolean isPublic) {
        this.repoId = repoId;
        this.installationId = installationId;
        this.fullIndexing = fullIndexing;
        this.isPublic = isPublic;
        this.status = JobStatus.PENDING;
    }

}
