package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.PullRequestReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestReviewsRepository extends JpaRepository<PullRequestReview, Long> {

}
