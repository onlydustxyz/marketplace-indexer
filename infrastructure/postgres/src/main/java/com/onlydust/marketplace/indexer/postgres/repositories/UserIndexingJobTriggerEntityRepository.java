package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.UserIndexingJobTriggerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserIndexingJobTriggerEntityRepository extends JpaRepository<UserIndexingJobTriggerEntity, Long> {
}
