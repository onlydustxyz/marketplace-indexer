package com.onlydust.marketplace.indexer.domain.models.clean.public_events;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPublicEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.public_events.RawPullRequestEventPayload;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Builder(access = AccessLevel.PRIVATE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class PublicEvent {
    @EqualsAndHashCode.Include
    @NonNull
    Long id;
    @NonNull
    Payload payload;

    public static Optional<PublicEvent> of(RawPublicEvent event) {
        final var payload = (switch (event.type()) {
            case "PullRequestEvent" -> map(event, RawPullRequestEventPayload.class, PullRequestEventPayload.class);
            default -> {
                LOGGER.debug("Unknown event type: {}", event.type());
                yield null;
            }
        });

        return Optional.ofNullable(payload).map(p -> PublicEvent.builder().id(event.id()).payload(p).build());
    }

    private static <FROM, TO extends Payload> TO map(RawPublicEvent event, Class<FROM> fromClass, Class<TO> toClass) {
        try {
            return toClass.getConstructor(fromClass).newInstance(event.payload(fromClass));
        } catch (Exception e) {
            throw OnlyDustException.internalServerError("Error mapping event: " + event.toString(), e);
        }
    }

    public static class Payload {

    }
}
