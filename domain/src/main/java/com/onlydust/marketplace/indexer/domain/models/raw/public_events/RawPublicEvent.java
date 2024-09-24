package com.onlydust.marketplace.indexer.domain.models.raw.public_events;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.ZonedDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RawPublicEvent(Long id, String type, User actor, Repo repo, User org, ZonedDateTime createdAt, Payload payload) {
    public record User(Long id, String login, String displayLogin, String gravatarId, String url, String avatarUrl) {
    }

    public record Repo(Long id, String name, String url) {
    }

    public static abstract class Payload {
    }
}
