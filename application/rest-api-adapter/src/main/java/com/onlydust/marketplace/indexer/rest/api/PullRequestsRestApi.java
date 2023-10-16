package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.PullRequestIndexer;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tags(@Tag(name = "Pull request"))
@AllArgsConstructor
public class PullRequestsRestApi implements PullRequestApi {
    private final PullRequestIndexer cachedPullRequestIndexer;

    @Override
    public ResponseEntity<Void> indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber) {
        cachedPullRequestIndexer.indexPullRequest(repoOwner, repoName, pullRequestNumber);
        return ResponseEntity.noContent().build();
    }
}
