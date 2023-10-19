package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.GithubAccountRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountEntityRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresGithubAccountRepository implements GithubAccountRepository {
    private final GithubAccountEntityRepository githubAccountEntityRepository;

    @Override
    public void removeInstallation(Long id) {
        githubAccountEntityRepository.removeInstallation(id);
    }
}
