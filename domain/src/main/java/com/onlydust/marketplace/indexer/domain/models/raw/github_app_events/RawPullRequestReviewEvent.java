package com.onlydust.marketplace.indexer.domain.models.raw.github_app_events;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlydust.marketplace.indexer.domain.models.raw.*;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawPullRequestReviewEvent extends JsonDocument {
    String action;
    RawCodeReview review;
    @JsonProperty("pull_request")
    RawShortPullRequest pullRequest;
    RawRepo repository;
    RawInstallation installation;
}
