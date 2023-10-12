package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.clean.Event;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class EventListenerComposite<T extends Event> implements EventListener<T> {
    private final List<EventListener<T>> listeners;

    @Override
    public void onEvent(T event) {
        listeners.forEach(l -> l.onEvent(event));
    }
}
