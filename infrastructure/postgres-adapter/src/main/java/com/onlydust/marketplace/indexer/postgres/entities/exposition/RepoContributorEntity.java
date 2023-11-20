package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
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

    Boolean hasCompletedContribution;

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
