package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubIssueEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubIssueRepository;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class PostgresIssueStorage implements IssueStorage {
    private final GithubIssueRepository issueRepository;

    @Override
    public void saveAll(GithubIssue... Issues) {
        issueRepository.saveAll(Arrays.stream(Issues).map(GithubIssueEntity::of).toList());
    }
}
