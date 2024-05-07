package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.io.Serializable;

@Entity
@EqualsAndHashCode
@Table(name = "repos_contributors", schema = "indexer_exp")
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RepoContributorEntity {
    @EmbeddedId
    Id id;

    Integer completedContributionCount;
    Integer totalContributionCount;

    @Embeddable
    @Value
    @NoArgsConstructor(force = true)
    @AllArgsConstructor
    @EqualsAndHashCode
    public static class Id implements Serializable {
        Long repoId;
        Long contributorId;
    }
}
