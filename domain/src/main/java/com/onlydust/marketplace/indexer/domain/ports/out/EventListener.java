package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.clean.Event;

public interface EventListener<T extends Event> {
    void onEvent(T event);
}
