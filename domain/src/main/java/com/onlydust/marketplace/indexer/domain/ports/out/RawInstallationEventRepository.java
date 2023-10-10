package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;

public interface RawInstallationEventRepository {
    void save(RawInstallationEvent event);
}
