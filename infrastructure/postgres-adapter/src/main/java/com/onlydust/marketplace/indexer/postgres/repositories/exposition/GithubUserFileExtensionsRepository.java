package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubUserFileExtensionEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;

public interface GithubUserFileExtensionsRepository extends BaseJpaRepository<GithubUserFileExtensionEntity, GithubUserFileExtensionEntity.PrimaryKey> {
    List<GithubUserFileExtensionEntity> findAll();
}
