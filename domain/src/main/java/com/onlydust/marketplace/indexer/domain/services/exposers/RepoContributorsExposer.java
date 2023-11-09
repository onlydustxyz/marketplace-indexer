package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoContributorsStorage;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class RepoContributorsExposer implements FullRepoIndexer {
    FullRepoIndexer indexer;
    RepoContributorsStorage repoContributorsStorage;

    private void expose(CleanRepo repo) {
        repoContributorsStorage.updateRepoContributors(repo.getId());
    }

    @Override
    public Optional<CleanRepo> indexFullRepo(Long repoId) {
        final var repo = indexer.indexFullRepo(repoId);
        repo.ifPresent(this::expose);
        return repo;
    }
}
