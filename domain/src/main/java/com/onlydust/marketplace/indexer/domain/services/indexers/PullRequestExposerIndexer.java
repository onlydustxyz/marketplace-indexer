package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import lombok.AllArgsConstructor;

import javax.transaction.Transactional;
import java.util.Optional;

@AllArgsConstructor
public class PullRequestExposerIndexer implements PullRequestIndexer {
    PullRequestIndexer indexer;
    Exposer<CleanPullRequest> exposer;

    @Override
    @Transactional
    public Optional<CleanPullRequest> indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber) {
        final var pullRequest = indexer.indexPullRequest(repoOwner, repoName, pullRequestNumber);
        pullRequest.ifPresent(exposer::expose);
        return pullRequest;
    }
}
