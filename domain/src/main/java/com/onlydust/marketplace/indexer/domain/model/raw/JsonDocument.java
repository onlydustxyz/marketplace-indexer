package com.onlydust.marketplace.indexer.domain.model.raw;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@EqualsAndHashCode
public class JsonDocument {
    @Getter(value = AccessLevel.NONE)
    Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    public void add(String property, Object value) {
        properties.put(property, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getProperties() {
        return properties;
    }
}
