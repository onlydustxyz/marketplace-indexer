package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.Date;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawRepo extends JsonDocument {
    Long id;
    String name;
    @JsonProperty("full_name")
    String fullName;
    RawShortAccount owner;
    @JsonProperty("html_url")
    String htmlUrl;
    String description;
    @JsonProperty("updated_at")
    Date updatedAt;
    @JsonProperty("stargazers_count")
    Long stargazersCount;
    @JsonProperty("forks_count")
    Long forksCount;
    @JsonProperty("has_issues")
    Boolean hasIssues;
    RawRepo parent;
    String visibility;
    @JsonProperty("private")
    Boolean isPrivate;
}
