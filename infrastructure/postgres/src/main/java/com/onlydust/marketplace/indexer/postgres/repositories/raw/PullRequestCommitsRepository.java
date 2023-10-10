package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.PullRequestCommits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestCommitsRepository extends JpaRepository<PullRequestCommits, Long> {

}
