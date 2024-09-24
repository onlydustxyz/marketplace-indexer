package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.raw.RawGithubAppEvent;

import java.util.Optional;

public interface EventInboxStorage {
    void save(RawGithubAppEvent event);

    Optional<RawGithubAppEvent> peek(String... types);

    void ack(Long id);

    void nack(Long id, String reason);

    void ignore(Long id, String reason);
}
