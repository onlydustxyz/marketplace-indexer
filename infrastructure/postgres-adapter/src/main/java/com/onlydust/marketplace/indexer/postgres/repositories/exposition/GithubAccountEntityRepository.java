package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface GithubAccountEntityRepository extends BaseJpaRepository<GithubAccountEntity, Long> {
}
