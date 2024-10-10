package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubUserFileExtensionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GithubUserFileExtensionsRepository extends JpaRepository<GithubUserFileExtensionEntity, GithubUserFileExtensionEntity.PrimaryKey> {
    Optional<GithubUserFileExtensionEntity> findByUserIdAndFileExtension(Long userId, String fileExtension);
}
