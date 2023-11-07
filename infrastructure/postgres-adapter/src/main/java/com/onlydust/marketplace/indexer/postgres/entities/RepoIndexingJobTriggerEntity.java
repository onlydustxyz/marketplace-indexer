package com.onlydust.marketplace.indexer.postgres.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@EqualsAndHashCode
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "repo_indexing_job_triggers", schema = "indexer")
public class RepoIndexingJobTriggerEntity {
    @Id
    Long repoId;

    Long installationId;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant techCreatedAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(nullable = false)
    Instant techUpdatedAt;

    public RepoIndexingJobTriggerEntity(Long repoId, Long installationId) {
        this.repoId = repoId;
        this.installationId = installationId;
    }
}
