package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class IssueExposerIndexer implements IssueIndexer {
    IssueIndexer indexer;
    Exposer<CleanIssue> exposer;

    @Override
    @Transactional
    public Optional<CleanIssue> indexIssue(String repoOwner, String repoName, Long issueNumber) {
        final var issue = indexer.indexIssue(repoOwner, repoName, issueNumber);
        issue.ifPresent(exposer::expose);
        return issue;
    }
}
