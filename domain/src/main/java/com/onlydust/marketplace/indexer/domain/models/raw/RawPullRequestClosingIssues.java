package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawPullRequestClosingIssues extends JsonDocument {
    @NonNull
    @EqualsAndHashCode.Exclude
    JsonNode data;

    public List<Long> issueNumbers() {
        return data.at("/repository/pullRequest/closingIssuesReferences/nodes")
                .findValuesAsText("number")
                .stream()
                .map(Long::parseLong)
                .toList();
    }
}
