package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class RepoExposerIndexer implements RepoIndexer {
    RepoIndexer indexer;
    Exposer<CleanRepo> exposer;

    @Override
    public Optional<CleanRepo> indexRepo(Long repoId) {
        final var repo = indexer.indexRepo(repoId);
        repo.ifPresent(exposer::expose);
        return repo;
    }

    @Override
    public Optional<CleanRepo> indexRepo(String repoOwner, String repoName) {
        final var repo = indexer.indexRepo(repoOwner, repoName);
        repo.ifPresent(exposer::expose);
        return repo;
    }
}
