package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "github_repos_stats", schema = "indexer_exp")
public class GithubRepoStatsEntity {
    @Id
    Long id;

    Instant lastIndexedAt;
}
