package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.UserPublicEventsIndexingJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserPublicEventsIndexingJobRepository extends JpaRepository<UserPublicEventsIndexingJobEntity, Long> {

}
