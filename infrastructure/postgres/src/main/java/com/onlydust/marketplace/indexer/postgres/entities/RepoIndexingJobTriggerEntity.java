package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@EqualsAndHashCode
@Builder
@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "repo_indexing_job_triggers", schema = "indexer")
public class RepoIndexingJobTriggerEntity {
    @Id
    Long repoId;

    Long installationId;

    public static RepoIndexingJobTriggerEntity of(RepoIndexingJobTrigger trigger) {
        return RepoIndexingJobTriggerEntity.builder()
                .installationId(trigger.installationId())
                .repoId(trigger.repoId())
                .build();
    }
}
