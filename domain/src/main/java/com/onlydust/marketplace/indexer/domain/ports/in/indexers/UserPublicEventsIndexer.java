package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.events.Event;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

public interface UserPublicEventsIndexer {
    Stream<Event> indexUser(Long userId, ZonedDateTime since);
}
