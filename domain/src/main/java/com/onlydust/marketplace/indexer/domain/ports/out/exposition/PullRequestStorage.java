package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubPullRequest;

public interface PullRequestStorage {
    void saveAll(GithubPullRequest... pullRequests);
}
