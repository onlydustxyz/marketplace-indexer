package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.RawEvent;

import java.util.Optional;

public interface EventInboxStorage {
    void save(RawEvent event);

    Optional<RawEvent> peek();

    void ack(Long id);

    void nack(Long id, String reason);

    void ignore(Long id, String reason);
}
