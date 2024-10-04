package com.onlydust.marketplace.indexer.domain.models.raw.public_events;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;
import lombok.experimental.Accessors;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
@Accessors(fluent = true)
public class RawPullRequestEventPayload extends RawPublicEvent.Payload {
    String action;
    Long number;
    @JsonProperty("pull_request")
    RawPullRequest pullRequest;
}
