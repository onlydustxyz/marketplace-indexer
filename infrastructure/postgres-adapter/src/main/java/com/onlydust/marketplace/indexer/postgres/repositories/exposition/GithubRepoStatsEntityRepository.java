package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoStatsEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface GithubRepoStatsEntityRepository extends BaseJpaRepository<GithubRepoStatsEntity, Long> {

}
