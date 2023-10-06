package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.RepoCheckRuns;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoCheckRunsRepository extends JpaRepository<RepoCheckRuns, RepoCheckRuns.Id> {

}
