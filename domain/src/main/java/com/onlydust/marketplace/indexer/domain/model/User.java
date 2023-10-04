package com.onlydust.marketplace.indexer.domain.model;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.HashMap;
import java.util.Map;

@Value
@NoArgsConstructor(force = true)
public class User {
    Integer id;
    String login;

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
