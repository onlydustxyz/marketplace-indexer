package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;

import java.util.Optional;

public interface PullRequestIndexer {
    Optional<CleanPullRequest> indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber);
}
