package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.UserPublicEventsIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserPublicEventsIndexingJobRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class PostgresUserPublicEventsIndexingJobStorage implements UserPublicEventsIndexingJobStorage {
    private final UserPublicEventsIndexingJobRepository repository;

    @Override
    @Transactional
    public void add(Long userId) {
        repository.merge(UserPublicEventsIndexingJobEntity.builder()
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
        return repository.findAll(Sort.by("userId")).stream()
                .map(UserPublicEventsIndexingJobEntity::userId)
                .collect(toSet());
    }

    @Override
    @Transactional
    public void startJob(Long userId) {
        repository.findById(userId).ifPresent(job -> job
                .status(JobStatus.RUNNING)
                .startedAt(Instant.now()));
    }

    @Override
    @Transactional
    public void failJob(Long userId) {
        repository.findById(userId).ifPresent(job -> job
                .status(JobStatus.FAILED)
                .finishedAt(Instant.now()));
    }

    @Override
    @Transactional
    public void endJob(Long userId) {
        repository.findById(userId).ifPresent(job -> job
                .status(JobStatus.SUCCESS)
                .finishedAt(Instant.now()));
    }
}
