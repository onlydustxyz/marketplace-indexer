package com.onlydust.marketplace.indexer.domain.ports.in.events;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;

public interface InstallationEventHandler {
    void process(RawInstallationEvent rawEvent);
}
