package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.PullRequestClosingIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestClosingIssueRepository extends JpaRepository<PullRequestClosingIssue, PullRequestClosingIssue.Id> {
}
