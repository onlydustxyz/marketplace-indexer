package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@ToString(callSuper = true)
@Builder
public class RawAccount extends JsonDocument {
    Long id;
    String login;
    String type;
    @JsonProperty("html_url")
    String htmlUrl;
    @JsonProperty("avatar_url")
    String avatarUrl;
    String name;
    String bio;
    String location;
    String blog;
    @JsonProperty("created_at")
    String createdAt;
}
