package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAppInstallationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubAppInstallationEntityRepository extends JpaRepository<GithubAppInstallationEntity, Long> {
    Optional<GithubAppInstallationEntity> findByAccountId(Long accountId);
}
