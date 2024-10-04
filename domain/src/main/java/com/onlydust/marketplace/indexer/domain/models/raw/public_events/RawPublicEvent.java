package com.onlydust.marketplace.indexer.domain.models.raw.public_events;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.raw.JsonDocument;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import lombok.EqualsAndHashCode;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RawPublicEvent(Long id,
                             @EqualsAndHashCode.Exclude String type,
                             @EqualsAndHashCode.Exclude RawAccount actor,
                             @EqualsAndHashCode.Exclude RawRepo repo,
                             @EqualsAndHashCode.Exclude RawAccount org,
                             @EqualsAndHashCode.Exclude ZonedDateTime createdAt,
                             @EqualsAndHashCode.Exclude JsonNode payload) {

    public Optional<Payload> decode() {
        return Optional.ofNullable(switch (type) {
            case "PullRequestEvent" -> payloadAs(RawPullRequestEventPayload.class);
            default -> null;
        });
    }

    private <T> T payloadAs(Class<T> type) {
        try {
            return new ObjectMapper().treeToValue(payload, type);
        } catch (IOException e) {
            throw OnlyDustException.internalServerError("Error deserializing event payload", e);
        }
    }

    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Payload extends JsonDocument {
    }
}
