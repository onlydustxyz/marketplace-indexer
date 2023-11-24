package com.onlydust.marketplace.indexer.postgres.entities;

import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

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

    Date suspendedAt;

    @Enumerated(EnumType.STRING)
    @Type(type = "job_status")
    JobStatus status;

    Instant startedAt;
    Instant finishedAt;

    public RepoIndexingJobEntity(Long repoId, Long installationId) {
        this.repoId = repoId;
        this.installationId = installationId;
        this.status = JobStatus.PENDING;
    }

}
