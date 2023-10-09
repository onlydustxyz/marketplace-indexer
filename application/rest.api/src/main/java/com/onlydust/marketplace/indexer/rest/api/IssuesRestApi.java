package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Issues"))
@AllArgsConstructor
public class IssuesRestApi implements IssuesApi {
    private final IndexingService onDemandIndexer;

    @Override
    public ResponseEntity<Void> indexIssue(String repoOwner, String repoName, Integer issueNumber) {
        onDemandIndexer.indexIssue(repoOwner, repoName, issueNumber.longValue());
        return ResponseEntity.noContent().build();
    }
}
