package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawRepoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RepoRepository extends JpaRepository<RawRepoEntity, Long> {
    Optional<RawRepoEntity> findByOwnerAndNameAndDeleted(String repoOwner, String repoName, Boolean deleted);
}
