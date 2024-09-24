package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.UserStatsIndexingJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStatsIndexingJobRepository extends JpaRepository<UserStatsIndexingJobEntity, Long> {

}
