package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawInstallationEventStorage;

import java.util.ArrayList;
import java.util.List;

public class RawInstallationEventStorageStub implements RawInstallationEventStorage {
    final List<RawInstallationEvent> events = new ArrayList<>();

    @Override
    public void save(RawInstallationEvent event) {
        events.add(event);
    }

    public List<RawInstallationEvent> events() {
        return events;
    }
}
