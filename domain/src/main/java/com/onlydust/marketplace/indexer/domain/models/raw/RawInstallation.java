package com.onlydust.marketplace.indexer.domain.models.raw;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawInstallation extends JsonDocument {
    Long id;
    RawUser account;
}