package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
                .stream().map(job -> new RepoIndexingJobTrigger(job.getRepoId(), job.getFullIndexing(), job.getIsPublic()))
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
    public void startJob(Long repoId) {
        final var job = repository.findById(repoId)
                .orElseThrow(() -> OnlyDustException.notFound("Job not found for repo %d".formatted(repoId)));
        repository.save(job.toBuilder()
                .status(JobStatus.RUNNING)
                .startedAt(Instant.now())
                .build());
    }

    @Override
    public void failJob(Long repoId) {
        final var job = repository.findById(repoId)
                .orElseThrow(() -> OnlyDustException.notFound("Job not found for repo %d".formatted(repoId)));
        repository.save(job.toBuilder()
                .status(JobStatus.FAILED)
                .finishedAt(Instant.now())
                .build());
    }

    @Override
    public void endJob(Long repoId) {
        final var job = repository.findById(repoId)
                .orElseThrow(() -> OnlyDustException.notFound("Job not found for repo %d".formatted(repoId)));
        repository.save(job.toBuilder()
                .status(JobStatus.SUCCESS)
                .finishedAt(Instant.now())
                .build());
    }

    @Override
    public void configureRepoForFullIndexing(Long repoId, Boolean isPublic) {
        final var job = repository.findById(repoId)
                .orElse(RepoIndexingJobEntity.builder()
                        .status(JobStatus.PENDING)
                        .repoId(repoId)
                        .isPublic(isPublic)
                        .build());

        repository.save(job.toBuilder()
                .fullIndexing(true)
                .build());
    }

    @Override
    public void setInstallationForRepos(Long installationId, RepoIndexingJobTrigger... triggers) {
        final var jobs = Arrays.stream(triggers).
                map(trigger -> repository.findById(trigger.getRepoId())
                        .orElse(RepoIndexingJobEntity.builder()
                                .repoId(trigger.getRepoId())
                                .status(JobStatus.PENDING)
                                .fullIndexing(false)
                                .isPublic(trigger.getIsPublic())
                                .build()))
                .map(job -> job.toBuilder()
                        .installationId(installationId)
                        .build())
                .toList();

        repository.saveAll(jobs);
    }

    @Override
    public void setPrivate(Long repoId) {
        final var job = repository.findById(repoId)
                .orElseThrow(() -> OnlyDustException.notFound("Job not found for repo %d".formatted(repoId)));
        repository.save(job.toBuilder()
                .isPublic(false)
                .build());
    }

    @Override
    public void setPublic(Long repoId) {
        final var job = repository.findById(repoId)
                .orElseThrow(() -> OnlyDustException.notFound("Job not found for repo %d".formatted(repoId)));
        repository.save(job.toBuilder()
                .isPublic(true)
                .build());
    }

    @Override
    public void configureRepoForLightIndexing(Long repoId) {
        repository.findById(repoId).ifPresent(
                job -> repository.save(job.toBuilder()
                        .fullIndexing(false)
                        .build()));
    }
}
