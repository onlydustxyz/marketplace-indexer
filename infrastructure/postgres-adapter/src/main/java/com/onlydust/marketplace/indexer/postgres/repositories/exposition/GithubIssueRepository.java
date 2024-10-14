package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

public interface GithubIssueRepository extends BaseJpaRepository<GithubIssueEntity, Long> {
}
