package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.events.GithubAppEvent;

import java.time.ZonedDateTime;
import java.util.stream.Stream;

public interface UserPublicEventsIndexer {
    Stream<GithubAppEvent> indexUser(Long userId, ZonedDateTime since);
}
