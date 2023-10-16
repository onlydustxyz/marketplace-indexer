package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.RepoCheckRuns;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoCheckRunsRepository extends JpaRepository<RepoCheckRuns, RepoCheckRuns.Id> {

}
