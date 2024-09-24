package com.onlydust.marketplace.indexer.domain.models.raw.github_app_events;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;

import java.io.IOException;

public record RawGithubAppEvent(Long id, String type, JsonNode payload) {
    public static RawGithubAppEvent of(String type, JsonNode jsonNode) {
        return new RawGithubAppEvent(null, type, jsonNode);
    }

    public <T> T payload(Class<T> type) {
        try {
            return new ObjectMapper().treeToValue(payload, type);
        } catch (IOException e) {
            throw OnlyDustException.internalServerError("Error deserializing event payload", e);
        }
    }
}
