package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubAccountEntityRepository extends JpaRepository<GithubAccountEntity, Long> {
}
