package com.onlydust.marketplace.indexer.domain.models.raw;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Map;
import java.util.stream.Collectors;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class RawLanguages extends JsonDocument {
    public Map<String, Long> get() {
        return getProperties().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, value -> Long.parseLong(value.getValue().toString())));
    }
}
