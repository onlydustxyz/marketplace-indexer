package com.onlydust.marketplace.indexer.domain.model.raw;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class RawRepo extends JsonDocument {
    Integer id;
    String name;
    RawUser owner;
}
