package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.UserPublicEventsIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserPublicEventsIndexingJobRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class PostgresUserPublicEventsIndexingJobStorage implements UserPublicEventsIndexingJobStorage {
    private final UserPublicEventsIndexingJobRepository repository;

    @Override
    public void add(Long userId) {
        repository.save(UserPublicEventsIndexingJobEntity.builder()
                .userId(userId)
                .build());
    }

    @Override
    public Optional<ZonedDateTime> lastEventTimestamp(Long userId) {
        return repository.findById(userId)
                .map(UserPublicEventsIndexingJobEntity::lastEventTimestamp);
    }

    @Override
    public Set<Long> all() {
        return repository.findAll().stream()
                .map(UserPublicEventsIndexingJobEntity::userId)
                .collect(toSet());
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
