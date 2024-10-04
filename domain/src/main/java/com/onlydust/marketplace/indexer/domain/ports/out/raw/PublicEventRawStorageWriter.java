package com.onlydust.marketplace.indexer.domain.ports.out.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;

public interface PublicEventRawStorageWriter {
    void savePublicEvent(RawPublicEvent rawEvent);
}
