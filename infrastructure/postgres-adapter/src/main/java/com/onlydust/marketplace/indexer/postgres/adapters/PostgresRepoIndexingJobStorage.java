package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.RepoIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.RepoIndexingJobEntityRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@AllArgsConstructor
public class PostgresRepoIndexingJobStorage implements RepoIndexingJobStorage {
    private final RepoIndexingJobEntityRepository repository;

    @Override
    public Set<Long> installationIds() {
        return repository.findAllValidInstallationIds();
    }

    @Override
    public Set<Long> reposUpdatedBefore(Long installationId, Instant since) {
        return repository.findReposUpdatedBefore(installationId, since);
    }

    @Override
    public void add(Long installationId, Long... repoIds) {
        repository.saveAll(Arrays.stream(repoIds).map(repoId -> new RepoIndexingJobEntity(repoId, installationId)).toList());
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
    public void setSuspendedAt(Long installationId, Instant suspendedAt) {
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
}
