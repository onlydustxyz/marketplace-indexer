package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.clean.Event;
import com.onlydust.marketplace.indexer.domain.ports.out.EventListener;

import java.util.ArrayList;
import java.util.List;

public class EventListenerStub<T extends Event> implements EventListener<T> {
    private final List<T> events = new ArrayList<>();

    @Override
    public void onEvent(T event) {
        events.add(event);
    }

    public List<T> events() {
        return events;
    }
}
