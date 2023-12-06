package com.onlydust.marketplace.indexer.domain.models.raw;


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
    byte[] payload;

    public <T> T payload(Class<T> type) {
        try {
            return new ObjectMapper().readValue(payload, type);
        } catch (IOException e) {
            throw OnlyDustException.internalServerError("Error deserializing event payload", e);
        }
    }

    public String toString() {
        return """
                {
                    "id": %d,
                    "type": "%s",
                    "payload": %s
                }
                """.formatted(id, type, new String(payload));
    }
}
