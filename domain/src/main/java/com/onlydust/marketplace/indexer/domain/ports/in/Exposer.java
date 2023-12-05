package com.onlydust.marketplace.indexer.domain.ports.in;

public interface Exposer<T> {
    void expose(T object);
}
