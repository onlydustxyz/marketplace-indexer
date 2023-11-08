package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubRepoStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoEntityRepository;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PostgresGithubRepoStorage implements GithubRepoStorage {
    private final GithubRepoEntityRepository githubRepoRepository;

    @Override
    public void saveAll(List<GithubRepo> repos) {
        githubRepoRepository.saveAll(repos.stream().map(GithubRepoEntity::of).toList());
    }
}
