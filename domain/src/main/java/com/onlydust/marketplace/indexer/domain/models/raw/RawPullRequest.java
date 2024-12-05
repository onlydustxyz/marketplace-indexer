package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
    RawShortAccount author;

    @JsonProperty("merged_by")
    RawShortAccount mergedBy;

    String title;
    String state;
    @JsonProperty("created_at")
    Date createdAt;
    @JsonProperty("closed_at")
    Date closedAt;
    @JsonProperty("merged_at")
    Date mergedAt;
    @JsonProperty("updated_at")
    Date updatedAt;
    @JsonProperty("html_url")
    String htmlUrl;
    String body;
    Integer comments;
    Boolean merged;
    Boolean draft;


    @JsonProperty("requested_reviewers")
    @Getter(AccessLevel.NONE)
    List<RawShortAccount> requestedReviewers;

    public List<RawShortAccount> getRequestedReviewers() {
        return requestedReviewers == null ? List.of() : requestedReviewers;
    }

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
