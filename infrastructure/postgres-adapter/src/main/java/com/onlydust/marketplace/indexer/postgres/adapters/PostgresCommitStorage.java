package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCommit;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.CommitStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubCommitEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubCommitRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NonNull;

@AllArgsConstructor
public class PostgresCommitStorage implements CommitStorage {
    private final GithubCommitRepository githubCommitRepository;

    @Override
    @Transactional
    public void save(@NonNull GithubCommit commit) {
        githubCommitRepository.merge(GithubCommitEntity.of(commit));
    }
}
