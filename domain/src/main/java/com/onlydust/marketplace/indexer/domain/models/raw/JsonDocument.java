package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
public class JsonDocument {
    Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    protected void add(String property, Object value) {
        properties.put(property, value);
    }

    @JsonAnyGetter
    protected Map<String, Object> getProperties() {
        return properties;
    }
}
