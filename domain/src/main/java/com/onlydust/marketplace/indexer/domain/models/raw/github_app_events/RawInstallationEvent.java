package com.onlydust.marketplace.indexer.domain.models.raw.github_app_events;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlydust.marketplace.indexer.domain.models.raw.JsonDocument;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallation;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
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
