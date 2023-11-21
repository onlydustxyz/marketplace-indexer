package com.onlydust.marketplace.indexer.domain.services.exposers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoContributorsStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class FullRepoExposer implements FullRepoIndexer {
    FullRepoIndexer indexer;
    RepoContributorsStorage repoContributorsStorage;

    private void expose(CleanRepo repo) {
        LOGGER.info("Exposing repo {}", repo.getId());
        repoContributorsStorage.updateRepoContributors(repo.getId());
    }

    @Override
    public Optional<CleanRepo> indexFullRepo(Long repoId) {
        final var repo = indexer.indexFullRepo(repoId);
        repo.ifPresentOrElse(this::expose, () -> LOGGER.warn("Repo {} not found, unable to expose", repoId));
        return repo;
    }
}
