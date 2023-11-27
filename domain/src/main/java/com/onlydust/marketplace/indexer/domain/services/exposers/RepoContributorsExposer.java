package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoContributorsStorage;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class RepoContributorsExposer implements RepoIndexer {
    RepoIndexer indexer;
    RepoContributorsStorage repoContributorsStorage;

    private void expose(CleanRepo repo) {
        repoContributorsStorage.updateRepoContributors(repo.getId());
    }

    @Override
    public Optional<CleanRepo> indexRepo(Long repoId) {
        final var repo = indexer.indexRepo(repoId);
        repo.ifPresent(this::expose);
        return repo;
    }

    @Override
    public Optional<CleanRepo> indexRepo(String repoOwner, String repoName) {
        final var repo = indexer.indexRepo(repoOwner, repoName);
        repo.ifPresent(this::expose);
        return repo;
    }
}
