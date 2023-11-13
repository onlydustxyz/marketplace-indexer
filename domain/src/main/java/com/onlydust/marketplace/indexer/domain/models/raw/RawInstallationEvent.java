package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawInstallationEvent extends JsonDocument {
    String action;
    RawInstallation installation;
    List<RawRepo> repositories;
    @JsonProperty("repositories_added")
    List<RawRepo> repositoriesAdded;
    @JsonProperty("repositories_removed")
    List<RawRepo> repositoriesRemoved;
}
