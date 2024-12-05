package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.JobStatus;
import com.onlydust.marketplace.indexer.postgres.entities.UserPublicEventsIndexingJobEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.ListPagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface UserPublicEventsIndexingJobRepository extends BaseJpaRepository<UserPublicEventsIndexingJobEntity, Long>,
        ListPagingAndSortingRepository<UserPublicEventsIndexingJobEntity, Long> {
    List<UserPublicEventsIndexingJobEntity> findAll();

    Optional<UserPublicEventsIndexingJobEntity> findByUserId(Long userIds);

    List<UserPublicEventsIndexingJobEntity> findAllByStatus(JobStatus status, Sort sort);
}
