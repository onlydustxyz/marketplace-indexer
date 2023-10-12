package com.onlydust.marketplace.indexer.postgres.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@EqualsAndHashCode
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "repo_indexing_job_triggers", schema = "indexer")
public class RepoIndexingJobTrigger {
    @Id
    Long repoId;

    Long installationId;
}
