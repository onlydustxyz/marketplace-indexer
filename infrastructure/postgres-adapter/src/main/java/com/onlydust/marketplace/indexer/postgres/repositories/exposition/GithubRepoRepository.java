package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubRepoRepository extends JpaRepository<GithubRepoEntity, Long> {
}
