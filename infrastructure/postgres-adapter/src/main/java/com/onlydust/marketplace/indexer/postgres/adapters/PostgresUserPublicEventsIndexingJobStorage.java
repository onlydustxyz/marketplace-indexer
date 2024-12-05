package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserPublicEventsIndexingJobStorage;
import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.UserPublicEventsIndexingJobEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.UserPublicEventsIndexingJobRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.Set;

import static com.onlydust.marketplace.indexer.postgres.entities.JobStatus.SUCCESS;
import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class PostgresUserPublicEventsIndexingJobStorage implements UserPublicEventsIndexingJobStorage {
    private final UserPublicEventsIndexingJobRepository repository;

    @Override
    @Transactional
    public void add(Long userId) {
        repository.findById(userId).ifPresentOrElse(
                job -> job.status(JobStatus.PENDING),
                () -> repository.persist(UserPublicEventsIndexingJobEntity.builder()
                        .userId(userId)
                        .build()));
    }

    @Override
    public Optional<ZonedDateTime> lastEventTimestamp(Long userId) {
        return repository.findByUserId(userId)
                .flatMap(j -> Optional.ofNullable(j.lastEventTimestamp()));
    }

    @Override
    public Set<Long> all() {
        return repository.findAllByStatus(SUCCESS, Sort.by("userId")).stream()
                .map(UserPublicEventsIndexingJobEntity::userId)
                .collect(toSet());
    }

    @Override
    @Transactional
    public void saveLastEventTimestamp(Long userId, @NonNull ZonedDateTime timestamp) {
        repository.findById(userId).ifPresent(job -> job.lastEventTimestamp(timestamp));
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
                .status(SUCCESS)
                .finishedAt(Instant.now()));
    }
}
