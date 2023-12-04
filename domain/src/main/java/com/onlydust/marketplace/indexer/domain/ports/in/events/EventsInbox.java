package com.onlydust.marketplace.indexer.domain.ports.in.events;

import com.onlydust.marketplace.indexer.domain.models.raw.RawEvent;

public interface EventsInbox {
    void push(RawEvent event);
}
