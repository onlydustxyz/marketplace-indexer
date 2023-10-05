package com.onlydust.marketplace.indexer.domain.models.raw;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class RawRepo extends JsonDocument {
    Long id;
    String name;
    RawUser owner;
}
