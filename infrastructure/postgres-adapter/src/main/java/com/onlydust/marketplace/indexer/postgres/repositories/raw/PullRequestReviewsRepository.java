package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPullRequestReviewEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestReviewsRepository extends JpaRepository<RawPullRequestReviewEntity, Long> {

}
