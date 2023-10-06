package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.PullRequestReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestReviewsRepository extends JpaRepository<PullRequestReview, Long> {

}
