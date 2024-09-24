package com.onlydust.marketplace.indexer.domain.models.raw.public_events;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record RawPublicEvent(Long id, String type, User actor, Repo repo, User org, ZonedDateTime createdAt, Payload payload) {
    public record User(Long id, String login, String displayLogin, String gravatarId, String url, String avatarUrl) {
    }

    public record Repo(Long id, String name, String url) {
    }

    public static abstract class Payload {
    }
}
