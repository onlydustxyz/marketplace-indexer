package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.IOException;

@Data
@AllArgsConstructor
public class RawEvent {
    Long id;
    String type;
    @Getter
    byte[] payload;

    public RawEvent(String type, byte[] payload) {
        this.type = type;
        this.payload = payload;
    }

    public <T> T payload(Class<T> type) {
        try {
            return new ObjectMapper().readValue(payload, type);
        } catch (IOException e) {
            throw OnlyDustException.internalServerError("Error deserializing event payload", e);
        }
    }
}
