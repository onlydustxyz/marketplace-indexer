package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawAccount extends JsonDocument {
    Long id;
    String login;
    String type;
    @JsonProperty("html_url")
    String htmlUrl;
    @JsonProperty("avatar_url")
    String avatarUrl;
}
