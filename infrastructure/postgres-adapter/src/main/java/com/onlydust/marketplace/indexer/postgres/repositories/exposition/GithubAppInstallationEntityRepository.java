package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAppInstallationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubAppInstallationEntityRepository extends JpaRepository<GithubAppInstallationEntity, Long> {
}
