package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
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

    @Override
    public void setPrivate(Long repoId) {
        final var repo = githubRepoEntityRepository.findById(repoId).orElseThrow(() -> OnlyDustException.notFound("Repo not found"));
        githubRepoEntityRepository.save(repo.toBuilder()
                .visibility(GithubRepoEntity.Visibility.PRIVATE)
                .build());
    }

    @Override
    public void setPublic(Long repoId) {
        final var repo = githubRepoEntityRepository.findById(repoId).orElseThrow(() -> OnlyDustException.notFound("Repo not found"));
        githubRepoEntityRepository.save(repo.toBuilder()
                .visibility(GithubRepoEntity.Visibility.PUBLIC)
                .build());
    }
}
