package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RawPullRequestClosingIssuesEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestClosingIssueRepository extends JpaRepository<RawPullRequestClosingIssuesEntity, RawPullRequestClosingIssuesEntity.Id> {
}
