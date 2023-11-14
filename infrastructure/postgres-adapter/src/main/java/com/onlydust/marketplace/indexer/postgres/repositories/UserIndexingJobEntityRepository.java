package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.UserIndexingJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserIndexingJobEntityRepository extends JpaRepository<UserIndexingJobEntity, Long> {
}
