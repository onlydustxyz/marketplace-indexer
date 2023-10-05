package com.onlydust.marketplace.indexer.domain.model.raw;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class RawPullRequest extends JsonDocument {
    Integer id;
    Integer number;
    Base base;

    @Value
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor(force = true)
    public static class Base extends JsonDocument {
        RawRepo repo;
    }
}
