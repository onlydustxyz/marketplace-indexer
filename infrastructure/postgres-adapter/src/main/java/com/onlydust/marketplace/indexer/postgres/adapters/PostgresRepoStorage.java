package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresRepoStorage implements RepoStorage {
    private final GithubRepoEntityRepository githubRepoEntityRepository;

    @Override
    public void save(GithubRepo repo) {
        githubRepoEntityRepository.save(GithubRepoEntity.of(repo));
    }
}
