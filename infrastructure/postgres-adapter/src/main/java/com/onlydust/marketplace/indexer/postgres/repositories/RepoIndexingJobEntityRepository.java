package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface RepoIndexingJobEntityRepository extends JpaRepository<RepoIndexingJobEntity, Long> {
    @Modifying
    @Query("UPDATE RepoIndexingJobEntity SET installationId = NULL WHERE installationId = :installationId")
    void deleteInstallationId(Long installationId);

    @Query("SELECT DISTINCT installationId FROM RepoIndexingJobEntity WHERE suspendedAt IS NULL AND finishedAt IS NULL OR finishedAt < :since")
    Set<Long> listOutdatedInstallationIds(Instant since);

    @Query("SELECT DISTINCT repoId FROM RepoIndexingJobEntity WHERE (:installationId IS NULL AND installationId IS NULL) OR (:installationId IS NOT NULL AND installationId = :installationId)")
    Set<Long> findDistinctRepoIdsByInstallationId(Long installationId);

    @Modifying
    @Query("UPDATE RepoIndexingJobEntity SET installationId = NULL WHERE installationId = :installationId AND repoId IN :repoIds")
    void deleteInstallationIdForRepos(Long installationId, List<Long> repoIds);

    @Modifying
    @Query("UPDATE RepoIndexingJobEntity SET suspendedAt = :suspendedAt WHERE installationId = :installationId")
    void setSuspendedAt(Long installationId, Instant suspendedAt);
}
