package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.time.Instant;
import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawPullRequest extends JsonDocument {
    Long id;
    Long number;
    Base base;
    Head head;

    @JsonProperty("user")
    RawShortAccount author;

    String title;
    String state;
    @JsonProperty("created_at")
    Instant createdAt;
    @JsonProperty("closed_at")
    Instant closedAt;
    @JsonProperty("merged_at")
    Instant mergedAt;
    @JsonProperty("updated_at")
    Instant updatedAt;
    @JsonProperty("html_url")
    String htmlUrl;
    String body;
    Integer comments;
    Boolean merged;
    Boolean draft;


    @JsonProperty("requested_reviewers")
    List<RawShortAccount> requestedReviewers;

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
