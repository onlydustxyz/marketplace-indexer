package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.raw.RawGithubAppEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventsInbox;
import com.onlydust.marketplace.indexer.domain.ports.out.EventInboxStorage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class EventInboxService implements EventsInbox {
    private final EventInboxStorage eventInboxStorage;

    @Override
    public void push(RawGithubAppEvent event) {
        eventInboxStorage.save(event);
    }
}
