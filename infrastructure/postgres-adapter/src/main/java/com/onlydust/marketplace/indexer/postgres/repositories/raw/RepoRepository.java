package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawRepoEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepoRepository extends BaseJpaRepository<RawRepoEntity, Long> {
    Optional<RawRepoEntity> findByOwnerAndNameAndDeleted(String repoOwner, String repoName, Boolean deleted);

    List<RawRepoEntity> findAll();
}
