package com.onlydust.marketplace.indexer.domain.ports.in.events;

import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;

public interface RepositoryEventHandler {
    void process(RawRepositoryEvent rawEvent);
}
