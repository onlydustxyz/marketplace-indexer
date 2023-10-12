package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class JsonDocument implements Serializable {
    Map<String, Object> properties = new HashMap<>();

    @JsonAnySetter
    protected void add(String property, Object value) {
        properties.put(property, value);
    }

    @JsonAnyGetter
    protected Map<String, Object> getProperties() {
        return properties;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JsonDocument that)) return false;

        final ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.USE_LONG_FOR_INTS, true);
        return mapper.valueToTree(this).equals(mapper.valueToTree(that));
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }

    @JsonIgnore
    public boolean isEmpty() {
        return properties.isEmpty();
    }
}
