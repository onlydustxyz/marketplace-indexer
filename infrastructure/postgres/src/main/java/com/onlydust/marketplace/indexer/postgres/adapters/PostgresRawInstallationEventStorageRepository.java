package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.RawInstallationEventRepository;
import com.onlydust.marketplace.indexer.postgres.entities.raw.InstallationEvent;
import com.onlydust.marketplace.indexer.postgres.repositories.raw.InstallationEventRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresRawInstallationEventStorageRepository implements RawInstallationEventRepository {
    final InstallationEventRepository installationEventRepository;

    @Override
    public void save(RawInstallationEvent event) {
        installationEventRepository.save(InstallationEvent.of(event));
    }
}
