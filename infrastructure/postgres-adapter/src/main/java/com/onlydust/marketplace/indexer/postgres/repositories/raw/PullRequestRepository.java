package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPullRequestEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;
import java.util.Optional;

public interface PullRequestRepository extends BaseJpaRepository<RawPullRequestEntity, Long> {
    List<RawPullRequestEntity> findAllByRepoId(Long repoId);

    Optional<RawPullRequestEntity> findByRepoIdAndNumber(Long repoId, Long prNumber);

    List<RawPullRequestEntity> findAll();
}
