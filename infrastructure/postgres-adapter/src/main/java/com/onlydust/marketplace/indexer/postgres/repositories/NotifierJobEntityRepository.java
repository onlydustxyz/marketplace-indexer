package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.NotifierJobEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotifierJobEntityRepository extends JpaRepository<NotifierJobEntity, Long> {
}
