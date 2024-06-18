package com.onlydust.marketplace.indexer.domain.models.raw;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.Date;
import java.util.Map;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawInstallation extends JsonDocument {
    Long id;
    RawShortAccount account;
    @JsonProperty("suspended_at")
    Date suspendedAt;
    Map<String, InstallationEvent.Permission> permissions;
}
