package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTrigger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoIndexingJobTriggerRepository extends JpaRepository<RepoIndexingJobTrigger, Long> {
    void deleteAllByInstallationId(Long installationId);
}
