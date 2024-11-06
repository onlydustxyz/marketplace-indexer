package com.onlydust.marketplace.indexer.domain.models.raw.public_events;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.raw.JsonDocument;
import lombok.*;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RawPublicEvent(@NonNull Long id,
                             @NonNull String type,
                             @NonNull Account actor,
                             @NonNull Repo repo,
                             Account org,
                             @NonNull ZonedDateTime createdAt,
                             @NonNull JsonNode payload) {

    public static RawPublicEvent fromJson(String json) {
        final var objectMapper = JsonMapper.builder().findAndAddModules().build();
        try {
            return objectMapper.readValue(json, RawPublicEvent.class);
        } catch (IOException e) {
            throw OnlyDustException.internalServerError("Error deserializing event", e);
        }
    }

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
