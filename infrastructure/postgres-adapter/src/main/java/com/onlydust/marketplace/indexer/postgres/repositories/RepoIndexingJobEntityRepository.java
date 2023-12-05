package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface RepoIndexingJobEntityRepository extends JpaRepository<RepoIndexingJobEntity, Long> {
    @Modifying
    @Query("UPDATE RepoIndexingJobEntity SET installationId = NULL WHERE installationId = :installationId")
    void deleteInstallationId(Long installationId);

    @Query(value = "SELECT DISTINCT CASE WHEN installation_suspended_at IS NULL THEN installation_id END FROM indexer.repo_indexing_jobs", nativeQuery = true)
    Set<Long> findAllValidInstallationIds();

    @Query("""
            SELECT j
            FROM RepoIndexingJobEntity j
            WHERE (:installationId IS NULL AND (j.installationId IS NULL OR j.installationSuspendedAt IS NOT NULL) AND j.isPublic = TRUE)
               OR (:installationId IS NOT NULL AND j.installationId = :installationId AND j.installationSuspendedAt IS NULL)
                AND (j.finishedAt IS NULL OR j.finishedAt < :since)
            """)
    Set<RepoIndexingJobEntity> findReposUpdatedBefore(Long installationId, Instant since);

    @Modifying
    @Query("UPDATE RepoIndexingJobEntity SET installationId = NULL WHERE installationId = :installationId AND repoId IN :repoIds")
    void deleteInstallationIdForRepos(Long installationId, List<Long> repoIds);

    @Modifying
    @Query("UPDATE RepoIndexingJobEntity SET installationSuspendedAt = :suspendedAt WHERE installationId = :installationId")
    void setSuspendedAt(Long installationId, Date suspendedAt);
}
