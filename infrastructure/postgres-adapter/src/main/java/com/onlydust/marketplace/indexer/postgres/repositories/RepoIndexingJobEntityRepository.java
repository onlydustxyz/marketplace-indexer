package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface RepoIndexingJobEntityRepository extends JpaRepository<RepoIndexingJobEntity, Long> {
    @Modifying
    @Query("UPDATE RepoIndexingJobEntity SET installationId = NULL WHERE installationId = :installationId")
    void deleteInstallationId(Long installationId);

    @Query(value = "SELECT DISTINCT CASE WHEN suspended_at IS NULL THEN installation_id END FROM indexer.repo_indexing_jobs", nativeQuery = true)
    Set<Long> findAllValidInstallationIds();

    @Query("""
            SELECT DISTINCT repoId
            FROM RepoIndexingJobEntity
            WHERE (:installationId IS NULL AND (installationId IS NULL OR suspendedAt IS NOT NULL))
               OR installationId = :installationId
                AND (finishedAt IS NULL OR finishedAt < :since)
            """)
    Set<Long> findReposUpdatedBefore(Long installationId, Instant since);

    @Modifying
    @Query("UPDATE RepoIndexingJobEntity SET installationId = NULL WHERE installationId = :installationId AND repoId IN :repoIds")
    void deleteInstallationIdForRepos(Long installationId, List<Long> repoIds);

    @Modifying
    @Query("UPDATE RepoIndexingJobEntity SET suspendedAt = :suspendedAt WHERE installationId = :installationId")
    void setSuspendedAt(Long installationId, ZonedDateTime suspendedAt);
}
