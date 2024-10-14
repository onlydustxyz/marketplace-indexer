package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubIssueRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresIssueStorage implements IssueStorage {
    private final GithubIssueRepository issueRepository;

    @Override
    public void save(GithubIssue issue) {
        issueRepository.merge(GithubIssueEntity.of(issue));
    }

    @Override
    public void delete(Long id) {
        issueRepository.deleteById(id);
    }
}
