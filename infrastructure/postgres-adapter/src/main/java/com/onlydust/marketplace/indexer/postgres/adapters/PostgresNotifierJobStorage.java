package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.NotifierJob;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.NotifierJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.NotifierJobEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.NotifierJobEntityRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
public class PostgresNotifierJobStorage implements NotifierJobStorage {
    private final NotifierJobEntityRepository repository;

    @Override
    public NotifierJob startJob() {
        final var job = repository.save(repository.findAll().stream().findFirst()
                .orElse(new NotifierJobEntity())
                .toBuilder()
                .status(JobStatus.RUNNING)
                .startedAt(Instant.now())
                .build());

        return new NotifierJob(job.getId(), job.getLastNotification());
    }

    @Override
    public void endJob(NotifierJob job) {
        final var existing = repository.findById(job.getId())
                .orElseThrow(() -> OnlyDustException.notFound("Job not found %d".formatted(job.getId())));
        repository.save(existing.toBuilder()
                .status(JobStatus.SUCCESS)
                .finishedAt(Instant.now())
                .lastNotification(job.getLastNotificationSentAt())
                .build());
    }

    @Override
    public void failJob(NotifierJob job) {
        final var existing = repository.findById(job.getId())
                .orElseThrow(() -> OnlyDustException.notFound("Job not found %d".formatted(job.getId())));
        repository.save(existing.toBuilder()
                .status(JobStatus.FAILED)
                .finishedAt(Instant.now())
                .build());
    }
}
