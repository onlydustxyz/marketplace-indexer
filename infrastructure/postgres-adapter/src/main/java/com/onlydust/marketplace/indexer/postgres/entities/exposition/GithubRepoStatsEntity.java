package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.Instant;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Table(name = "github_repos_stats", schema = "indexer_exp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GithubRepoStatsEntity {
    @Id
    Long id;

    Instant lastIndexedAt;
}
