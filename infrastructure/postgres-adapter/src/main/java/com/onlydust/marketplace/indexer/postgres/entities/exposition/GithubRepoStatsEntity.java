package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_repos_stats", schema = "indexer_exp")
public class GithubRepoStatsEntity {
    @Id
    Long id;

    Instant lastIndexedAt;
}
