package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.PullRequestCommits;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PullRequestCommitsRepository extends JpaRepository<PullRequestCommits, Long> {

}
