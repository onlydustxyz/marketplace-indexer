package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAppInstallationEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;
import java.util.Optional;

public interface GithubAppInstallationEntityRepository extends BaseJpaRepository<GithubAppInstallationEntity, Long> {
    Optional<GithubAppInstallationEntity> findByAccountId(Long accountId);

    List<GithubAppInstallationEntity> findAll();
}
