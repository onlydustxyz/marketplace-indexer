package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.services.IndexingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Pull request"))
@AllArgsConstructor
public class PullRequestsRestApi implements PullRequestApi {
    private final IndexingService onDemandIndexer;

    @Override
    public ResponseEntity<Void> indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber) {
        onDemandIndexer.indexPullRequest(repoOwner, repoName, pullRequestNumber);
        return ResponseEntity.noContent().build();
    }
}
