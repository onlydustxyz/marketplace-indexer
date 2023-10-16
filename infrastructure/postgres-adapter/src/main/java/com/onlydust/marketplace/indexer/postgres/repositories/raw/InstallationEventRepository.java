package com.onlydust.marketplace.indexer.postgres.repositories.raw;

import com.onlydust.marketplace.indexer.postgres.entities.raw.InstallationEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InstallationEventRepository extends JpaRepository<InstallationEvent, Long> {
}
