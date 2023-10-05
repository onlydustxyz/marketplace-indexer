package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class RawPullRequest extends JsonDocument {
    Long id;
    Long number;
    Base base;
    Head head;

    @JsonProperty("user")
    RawUser author;

    @JsonProperty("requested_reviewers")
    List<RawUser> requestedReviewers;

    @Value
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor(force = true)
    public static class Base extends JsonDocument {
        RawRepo repo;
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    @NoArgsConstructor(force = true)
    public static class Head extends JsonDocument {
        String sha;
        RawRepo repo;
    }
}
