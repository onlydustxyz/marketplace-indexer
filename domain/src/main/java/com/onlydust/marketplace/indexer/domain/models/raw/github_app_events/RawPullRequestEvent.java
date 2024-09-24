package com.onlydust.marketplace.indexer.domain.models.raw.github_app_events;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlydust.marketplace.indexer.domain.models.raw.JsonDocument;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallation;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawPullRequestEvent extends JsonDocument {
    String action;
    @JsonProperty("pull_request")
    RawPullRequest pullRequest;
    RawRepo repository;
    RawInstallation installation;
}
