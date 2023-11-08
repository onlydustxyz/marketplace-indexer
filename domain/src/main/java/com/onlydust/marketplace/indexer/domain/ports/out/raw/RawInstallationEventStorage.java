package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;

public interface RawInstallationEventStorage {
    void save(RawInstallationEvent event);
}
