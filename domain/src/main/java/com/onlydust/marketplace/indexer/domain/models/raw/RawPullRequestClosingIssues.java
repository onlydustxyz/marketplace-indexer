package com.onlydust.marketplace.indexer.domain.models.raw;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;

import java.util.List;
import java.util.stream.StreamSupport;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawPullRequestClosingIssues extends JsonDocument {
    @NonNull
    @EqualsAndHashCode.Exclude
    JsonNode data;

    public List<IssueReference> issues() {
        final var nodes = data.at("/repository/pullRequest/closingIssuesReferences/nodes");
        return StreamSupport.stream(nodes.spliterator(), false)
                .map(node -> new IssueReference(
                        node.at("/repository/owner/login").asText(),
                        node.at("/repository/name").asText(),
                        node.at("/number").asLong()
                )).toList();
    }

    public record IssueReference(String repoOwner, String repoName, Long number) {
    }
}
