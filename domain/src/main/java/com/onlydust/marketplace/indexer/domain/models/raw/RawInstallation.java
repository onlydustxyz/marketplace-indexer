package com.onlydust.marketplace.indexer.domain.models.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.time.Instant;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawInstallation extends JsonDocument {
    Long id;
    RawShortAccount account;
    @JsonProperty("suspended_at")
    Instant suspendedAt;
}
