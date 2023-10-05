package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class RawIssue extends JsonDocument {
    Long id;
    Long number;
    List<RawUser> assignees;

    @JsonProperty("repository_url")
    String repositoryUrl;
}
