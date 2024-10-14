package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubCommitEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface GithubCommitRepository extends BaseJpaRepository<GithubCommitEntity, String> {
}
