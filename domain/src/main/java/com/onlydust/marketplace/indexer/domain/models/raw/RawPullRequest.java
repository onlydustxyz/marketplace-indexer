package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.Date;
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
    RawAccount author;

    String title;
    String state;
    @JsonProperty("created_at")
    Date createdAt;
    @JsonProperty("closed_at")
    Date closedAt;
    @JsonProperty("merged_at")
    Date mergedAt;
    @JsonProperty("html_url")
    String htmlUrl;
    String body;
    Integer comments;
    Boolean merged;


    @JsonProperty("requested_reviewers")
    List<RawAccount> requestedReviewers;

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
