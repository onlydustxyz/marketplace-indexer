package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;

public interface IssueIndexer {
    CleanIssue indexIssue(String repoOwner, String repoName, Long issueNumber);
}
