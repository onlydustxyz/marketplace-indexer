package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;

public interface PullRequestIndexer {
    CleanPullRequest indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber);
}
