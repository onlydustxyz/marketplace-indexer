package com.onlydust.marketplace.indexer.rest.api;

import com.onlydust.marketplace.indexer.domain.ports.in.contexts.AuthorizationContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
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
    private final AuthorizationContext authorizationContext;

    @Override
    public ResponseEntity<Void> indexIssue(String repoOwner, String repoName, Long issueNumber, String authorization) {
        authorizationContext.withAuthorization(authorization,
                () -> cachedIssueIndexer.indexIssue(repoOwner, repoName, issueNumber));
        return ResponseEntity.noContent().build();
    }
}
