package com.onlydust.marketplace.indexer.domain.models.clean.events;

import com.onlydust.marketplace.indexer.domain.models.raw.JsonDocument;
import com.onlydust.marketplace.indexer.domain.models.raw.RawEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawStarEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class Event {
    public static Optional<Event> of(RawEvent event) {
        return switch (event.getType()) {
            case "installation", "installation_repositories" -> map(event, RawInstallationEvent.class, InstallationEvent.class);
            case "star" -> map(event, RawStarEvent.class, StarEvent.class);
            default -> {
                LOGGER.debug("Unknown event type: {}", event.getType());
                yield Optional.empty();
            }
        };
    }

    public static <FROM extends JsonDocument, TO extends Event> Optional<Event> map(RawEvent event, Class<FROM> from, Class<TO> to) {
        try {
            return Optional.of(to.getConstructor(from).newInstance(event.payload(from)));
        } catch (Exception e) {
            LOGGER.error("Error mapping event: {}", event.toString(), e);
            return Optional.empty();
        }
    }
}
