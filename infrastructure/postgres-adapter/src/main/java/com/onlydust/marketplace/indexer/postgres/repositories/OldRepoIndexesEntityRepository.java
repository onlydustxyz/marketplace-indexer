package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.OldRepoIndexesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OldRepoIndexesEntityRepository extends JpaRepository<OldRepoIndexesEntity, Long> {
}
