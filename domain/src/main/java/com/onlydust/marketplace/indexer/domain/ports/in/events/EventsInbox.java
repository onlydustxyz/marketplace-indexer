package com.onlydust.marketplace.indexer.domain.ports.in.events;

import com.onlydust.marketplace.indexer.domain.models.raw.RawGithubAppEvent;

public interface EventsInbox {
    void push(RawGithubAppEvent event);
}
