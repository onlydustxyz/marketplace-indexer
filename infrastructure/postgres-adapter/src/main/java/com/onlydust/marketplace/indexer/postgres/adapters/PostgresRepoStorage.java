package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubRepoStatsEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubRepoStatsEntityRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
public class PostgresRepoStorage implements RepoStorage {
    private final GithubRepoRepository githubRepoRepository;
    private final GithubRepoStatsEntityRepository githubRepoStatsEntityRepository;

    @Override
    public void setLastIndexedTime(Long repoId, Instant lastIndexedTime) {
        githubRepoStatsEntityRepository.merge(GithubRepoStatsEntity.builder()
                .id(repoId)
                .lastIndexedAt(lastIndexedTime)
                .build());
    }

    @Override
    @Transactional
    public void save(GithubRepo repo) {
        githubRepoRepository.merge(GithubRepoEntity.of(repo));
    }
}
