package com.onlydust.marketplace.indexer.domain.models.raw.public_events;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.raw.JsonDocument;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RawPublicEvent(Long id,
                             String type,
                             Account actor,
                             Repo repo,
                             Account org,
                             ZonedDateTime createdAt,
                             JsonNode payload) {

    public Optional<Payload> decode() {
        return Optional.ofNullable(switch (type) {
            case "PullRequestEvent" -> payloadAs(RawPullRequestEventPayload.class);
            case "PushEvent" -> payloadAs(RawPushEventPayload.class);
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

    public static class Payload extends JsonDocument {
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor(force = true)
    @ToString(callSuper = true)
    public static class Account extends JsonDocument {
        Long id;
        String login;
        String url;
        String avatarUrl;
    }


    @Value
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor(force = true)
    @ToString(callSuper = true)
    public static class Repo extends JsonDocument {
        Long id;
        @JsonProperty("name")
        String fullName;
        String url;
    }
}
