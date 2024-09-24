package com.onlydust.marketplace.indexer.domain.models.clean.public_events;

import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class PublicEvent {

    public static Optional<PublicEvent> of(RawPublicEvent event) {
        return switch (event.type()) {
            default -> {
                LOGGER.debug("Unknown event type: {}", event.type());
                yield Optional.empty();
            }
        };
    }

    public static <TO extends PublicEvent> Optional<TO> map(RawPublicEvent event, Class<TO> to) {
        try {
            return Optional.of(to.getConstructor(RawPublicEvent.class).newInstance(event));
        } catch (Exception e) {
            LOGGER.error("Error mapping event: {}", event.toString(), e);
            return Optional.empty();
        }
    }
}
