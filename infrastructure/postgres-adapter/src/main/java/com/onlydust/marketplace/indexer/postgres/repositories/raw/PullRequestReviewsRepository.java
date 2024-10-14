package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPullRequestReviewEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;

import java.util.List;

public interface PullRequestReviewsRepository extends BaseJpaRepository<RawPullRequestReviewEntity, Long> {
    List<RawPullRequestReviewEntity> findAll();
}
