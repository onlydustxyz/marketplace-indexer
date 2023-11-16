package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubPullRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubPullRequestRepository extends JpaRepository<GithubPullRequestEntity, Long> {
}
