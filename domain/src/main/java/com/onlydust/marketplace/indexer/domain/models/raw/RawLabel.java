package com.onlydust.marketplace.indexer.domain.models.raw;


import lombok.*;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawLabel extends JsonDocument {
    @NonNull Long id;
    @NonNull String name;
    String description;
}
