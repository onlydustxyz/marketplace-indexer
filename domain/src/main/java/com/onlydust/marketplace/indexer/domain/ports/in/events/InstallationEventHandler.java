package com.onlydust.marketplace.indexer.domain.ports.in.events;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;

public interface InstallationEventHandler {
    void process(RawInstallationEvent rawEvent);

    void process(RawRepositoryEvent rawEvent);
}
