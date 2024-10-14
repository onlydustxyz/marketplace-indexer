package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.notFound;

@AllArgsConstructor
public class PostgresRepoIndexingJobStorage implements RepoIndexingJobStorage {
    private final RepoIndexingJobEntityRepository repository;

    @Override
    public Set<Long> installationIds() {
        return repository.findAllValidInstallationIds();
    }

    @Override
    public Set<RepoIndexingJobTrigger> reposUpdatedBefore(Long installationId, Instant since) {
        return repository.findReposUpdatedBefore(installationId, since)
                .stream().map(job -> new RepoIndexingJobTrigger(job.repoId(), job.fullIndexing(), job.isPublic()))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public void deleteInstallation(Long installationId) {
        repository.deleteInstallationId(installationId);
    }

    @Override
    public void deleteInstallationForRepos(Long installationId, List<Long> repoIds) {
        repository.deleteInstallationIdForRepos(installationId, repoIds);
    }

    @Override
    public void setSuspendedAt(Long installationId, Date suspendedAt) {
        repository.setSuspendedAt(installationId, suspendedAt);
    }

    @Override
    @Transactional
    public void startJob(Long repoId) {
        job(repoId)
                .status(JobStatus.RUNNING)
                .startedAt(Instant.now());
    }

    @Override
    @Transactional
    public void failJob(Long repoId) {
        job(repoId)
                .status(JobStatus.FAILED)
                .finishedAt(Instant.now());
    }

    @Override
    @Transactional
    public void endJob(Long repoId) {
        job(repoId)
                .status(JobStatus.SUCCESS)
                .finishedAt(Instant.now());
    }

    @Override
    @Transactional
    public void configureReposForFullIndexing(Set<Long> repoIds, Boolean isPublic) {
        repoIds.forEach(repoId -> repository.findById(repoId).ifPresentOrElse(
                job -> job.fullIndexing(true),
                () -> repository.persist(RepoIndexingJobEntity.builder()
                        .status(JobStatus.PENDING)
                        .repoId(repoId)
                        .isPublic(isPublic)
                        .fullIndexing(true)
                        .build()))
        );
    }

    @Override
    @Transactional
    public void setInstallationForRepos(Long installationId, Set<RepoIndexingJobTrigger> triggers) {
        triggers.forEach(trigger -> repository.findById(trigger.getRepoId()).ifPresentOrElse(
                job -> job
                        .isPublic(trigger.getIsPublic())
                        .installationId(installationId),
                () -> repository.persist(RepoIndexingJobEntity.builder()
                        .repoId(trigger.getRepoId())
                        .status(JobStatus.PENDING)
                        .fullIndexing(false)
                        .isPublic(trigger.getIsPublic())
                        .installationId(installationId)
                        .build()))
        );
    }

    @Override
    @Transactional
    public void setPrivate(Long repoId) {
        job(repoId).isPublic(false);
    }

    @Override
    @Transactional
    public void setPublic(Long repoId) {
        job(repoId).isPublic(true);
    }

    @Override
    @Transactional
    public void configureRepoForLightIndexing(Set<Long> repoIds) {
        repository.findAllById(repoIds)
                .forEach(job -> job.fullIndexing(false));
    }

    @Override
    public void delete(Long repoId) {
        repository.deleteById(repoId);
    }

    private RepoIndexingJobEntity job(Long repoId) {
        return repository.findById(repoId)
                .orElseThrow(() -> notFound("Job not found for repo %d".formatted(repoId)));
    }
}
