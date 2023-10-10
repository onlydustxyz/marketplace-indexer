package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.RawInstallationEventRepository;
import com.onlydust.marketplace.indexer.postgres.entities.InstallationEvent;
import com.onlydust.marketplace.indexer.postgres.repositories.InstallationEventRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresRawInstallationEventStorageRepository implements RawInstallationEventRepository {
    final InstallationEventRepository installationEventRepository;

    @Override
    public void save(RawInstallationEvent event) {
        installationEventRepository.save(InstallationEvent.of(event));
    }
}
