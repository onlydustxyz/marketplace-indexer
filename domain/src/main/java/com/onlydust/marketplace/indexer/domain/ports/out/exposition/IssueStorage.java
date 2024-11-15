package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;

public interface IssueStorage {
    void save(GithubIssue issue);

    void delete(Long id);

    void saveAssignee(Long issueId, GithubAccount assignee, GithubAccount assignedBy);

    void deleteAssignee(Long issueId, Long assigneeId);
}
