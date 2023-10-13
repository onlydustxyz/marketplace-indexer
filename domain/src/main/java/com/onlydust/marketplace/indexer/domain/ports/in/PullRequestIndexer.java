package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.PullRequest;

public interface PullRequestIndexer {
    PullRequest indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber);
}
