package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoContributorsStorage;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.RepoContributorRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PostgresRepoContributorsStorage implements RepoContributorsStorage {
    private final RepoContributorRepository repoContributorRepository;

    @Override
    @Transactional
    public void updateRepoContributors(Long repoId) {
        repoContributorRepository.deleteAllByRepoId(repoId);
        repoContributorRepository.insertAllByRepoId(repoId);
    }
}
