package com.onlydust.marketplace.indexer.postgres.entities;

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
@Table(name = "github_repo_indexes", schema = "public")
public class OldRepoIndexesEntity {
    @Id
    Long repoId;
}
