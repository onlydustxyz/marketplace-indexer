package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobTriggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepoIndexingJobTriggerEntityRepository extends JpaRepository<RepoIndexingJobTriggerEntity, Long> {
    void deleteAllByInstallationId(Long installationId);
}
