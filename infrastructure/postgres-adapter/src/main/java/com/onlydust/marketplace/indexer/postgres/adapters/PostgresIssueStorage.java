package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueAssigneeEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubIssueAssigneeRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubIssueRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresIssueStorage implements IssueStorage {
    private final GithubIssueRepository issueRepository;
    private final GithubIssueAssigneeRepository assigneeRepository;

    @Override
    public void save(GithubIssue issue) {
        issueRepository.merge(GithubIssueEntity.of(issue));
    }

    @Override
    public void delete(Long id) {
        assigneeRepository.deleteByIssueId(id);
        issueRepository.deleteById(id);
    }

    @Override
    public void saveAssignee(Long issueId, GithubAccount assignee, GithubAccount assignedBy) {
        assigneeRepository.merge(GithubIssueAssigneeEntity.of(issueId, assignee, assignedBy));
    }

    @Override
    public void deleteAssignee(Long issueId, Long assigneeId) {
        assigneeRepository.deleteByIssueIdAndUserId(issueId, assigneeId);
    }
}
