package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;

import java.util.Optional;

public interface IssueIndexer {
    Optional<CleanIssue> indexIssue(String repoOwner, String repoName, Long issueNumber);
}
