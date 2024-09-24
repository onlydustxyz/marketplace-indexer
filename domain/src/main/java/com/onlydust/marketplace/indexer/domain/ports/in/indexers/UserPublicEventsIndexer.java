package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.public_events.PublicEvent;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

public interface UserPublicEventsIndexer {
    Stream<PublicEvent> indexUser(Long userId, ZonedDateTime since);
}
