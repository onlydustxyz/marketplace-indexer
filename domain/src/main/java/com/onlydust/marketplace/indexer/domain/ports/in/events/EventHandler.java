package com.onlydust.marketplace.indexer.domain.ports.in.events;

public interface EventHandler<E> {
    void process(E rawEvent);
}
