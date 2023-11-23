package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.UserIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserIndexingJobEntityRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Set;

@AllArgsConstructor
public class PostgresUserIndexingJobStorage implements UserIndexingJobStorage {
    private final UserIndexingJobEntityRepository repository;

    @Override
    public Set<Long> usersUpdatedBefore(Instant since) {
        return repository.findUsersUpdatedBefore(since);
    }

    @Override
    public void add(Long userId) {
        repository.save(new UserIndexingJobEntity(userId));
    }

    @Override
    public void startJob(Long userId) {
        repository.findById(userId).ifPresent(job ->
                repository.save(job.toBuilder()
                        .status(JobStatus.RUNNING)
                        .startedAt(Instant.now())
                        .build()));
    }

    @Override
    public void failJob(Long userId) {
        repository.findById(userId).ifPresent(job ->
                repository.save(job.toBuilder()
                        .status(JobStatus.FAILED)
                        .finishedAt(Instant.now())
                        .build()));
    }

    @Override
    public void endJob(Long userId) {
        repository.findById(userId).ifPresent(job ->
                repository.save(job.toBuilder()
                        .status(JobStatus.SUCCESS)
                        .finishedAt(Instant.now())
                        .build()));
    }
}
