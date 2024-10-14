package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;

public interface GithubRepoRepository extends BaseJpaRepository<GithubRepoEntity, Long> {
    List<GithubRepoEntity> findAll();
}
