package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.IOException;

@Data
@AllArgsConstructor
public class RawEvent {
    Long id;
    String type;
    JsonNode payload;

    public static RawEvent of(String type, JsonNode jsonNode) {
        return new RawEvent(null, type, jsonNode);
    }

    public <T> T payload(Class<T> type) {
        try {
            return new ObjectMapper().treeToValue(payload, type);
        } catch (IOException e) {
            throw OnlyDustException.internalServerError("Error deserializing event payload", e);
        }
    }
}
