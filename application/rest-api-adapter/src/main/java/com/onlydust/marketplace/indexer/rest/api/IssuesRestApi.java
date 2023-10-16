package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.IssueIndexer;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Issues"))
@AllArgsConstructor
public class IssuesRestApi implements IssuesApi {
    private final IssueIndexer cachedIssueIndexer;

    @Override
    public ResponseEntity<Void> indexIssue(String repoOwner, String repoName, Long issueNumber) {
        cachedIssueIndexer.indexIssue(repoOwner, repoName, issueNumber);
        return ResponseEntity.noContent().build();
    }
}
