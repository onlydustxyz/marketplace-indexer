package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubUserFileExtensionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubUserFileExtensionsRepository extends JpaRepository<GithubUserFileExtensionEntity, GithubUserFileExtensionEntity.PrimaryKey> {
}
