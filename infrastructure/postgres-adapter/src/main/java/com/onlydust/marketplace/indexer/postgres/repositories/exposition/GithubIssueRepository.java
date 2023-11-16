package com.onlydust.marketplace.indexer.postgres.repositories.exposition;

import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GithubIssueRepository extends JpaRepository<GithubIssueEntity, Long> {
}
