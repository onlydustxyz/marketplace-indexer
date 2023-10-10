package com.onlydust.marketplace.indexer.postgres.repositories;

import com.onlydust.marketplace.indexer.postgres.entities.InstallationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstallationEventRepository extends JpaRepository<InstallationEvent, Long> {
}
