package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.UserIndexingJobEntity;
import io.hypersistence.utils.spring.repository.BaseJpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public interface UserIndexingJobEntityRepository extends BaseJpaRepository<UserIndexingJobEntity, Long> {
    @Query("""
            SELECT DISTINCT userId
            FROM UserIndexingJobEntity
            WHERE (finishedAt IS NULL OR finishedAt < :since)
            """)
    Set<Long> findUsersUpdatedBefore(Instant since);

    List<UserIndexingJobEntity> findAll();
}
