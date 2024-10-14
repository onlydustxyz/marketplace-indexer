package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubCommitEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;

public interface GithubCommitRepository extends BaseJpaRepository<GithubCommitEntity, String> {
    List<GithubCommitEntity> findAll();
}
