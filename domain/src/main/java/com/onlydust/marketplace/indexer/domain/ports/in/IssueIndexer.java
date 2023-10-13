package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.Issue;

public interface IssueIndexer {
    Issue indexIssue(String repoOwner, String repoName, Long issueNumber);
}
