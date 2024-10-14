package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.AccountStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GithubAccountEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GithubAccountEntityRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresAccountStorage implements AccountStorage {
    private final GithubAccountEntityRepository githubAccountEntityRepository;

    @Override
    public void save(GithubAccount account) {
        githubAccountEntityRepository.merge(GithubAccountEntity.of(account));
    }
}
