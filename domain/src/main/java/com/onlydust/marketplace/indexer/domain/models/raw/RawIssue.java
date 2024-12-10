package com.onlydust.marketplace.indexer.domain.models.raw;


import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.*;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawIssue extends JsonDocument {
    Long id;
    Long number;
    String title;
    @JsonProperty("user")
    RawShortAccount author;
    String state;
    @JsonProperty("state_reason")
    String stateReason;
    @JsonProperty("html_url")
    String htmlUrl;
    String body;
    @JsonProperty("created_at")
    Date createdAt;
    @JsonProperty("closed_at")
    Date closedAt;
    @JsonProperty("updated_at")
    Date updatedAt;
    @JsonProperty("pull_request")
    RawPullRequest pullRequest;
    Integer comments;
    
    @Getter(AccessLevel.NONE)
    List<RawShortAccount> assignees;
    public List<RawShortAccount> getAssignees() {
        return assignees != null ? assignees : List.of();
    }

    @NonNull List<RawLabel> labels;
}
