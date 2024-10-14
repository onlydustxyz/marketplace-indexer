package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubPullRequest;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.PullRequestStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubPullRequestEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubPullRequestRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class PostgresPullRequestStorage implements PullRequestStorage {
    private final GithubPullRequestRepository githubPullRequestRepository;

    @Override
    @Transactional
    public void saveAll(GithubPullRequest... pullRequests) {
        githubPullRequestRepository.mergeAll(Arrays.stream(pullRequests).map(GithubPullRequestEntity::of).toList());
    }
}
