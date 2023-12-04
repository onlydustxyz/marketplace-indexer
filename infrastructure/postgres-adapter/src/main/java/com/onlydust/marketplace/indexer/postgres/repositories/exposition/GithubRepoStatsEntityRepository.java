package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoStatsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubRepoStatsEntityRepository extends JpaRepository<GithubRepoStatsEntity, Long> {

}
