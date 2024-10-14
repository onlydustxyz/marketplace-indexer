package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubPullRequestEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;

public interface GithubPullRequestRepository extends BaseJpaRepository<GithubPullRequestEntity, Long> {
    List<GithubPullRequestEntity> findAll();
}
