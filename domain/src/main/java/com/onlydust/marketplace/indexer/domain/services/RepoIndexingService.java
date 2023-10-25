package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class RepoIndexingService implements RepoIndexer {
    private final RawStorageReader rawStorageReader;
    private final UserIndexer userIndexer;

    @Override
    public CleanRepo indexRepo(Long repoId) {
        LOGGER.info("Indexing repo {}", repoId);
        final var repo = rawStorageReader.repo(repoId).orElseThrow(() -> OnlyDustException.notFound("Repo not found"));
        return buildCleanRepo(repo);
    }

    @Override
    public CleanRepo indexRepo(String repoOwner, String repoName) {
        LOGGER.info("Indexing repo {}/{}", repoOwner, repoName);
        final var repo = rawStorageReader.repo(repoOwner, repoName).orElseThrow(() -> OnlyDustException.notFound("Repo not found"));
        return buildCleanRepo(repo);
    }

    private CleanRepo buildCleanRepo(RawRepo repo) {
        final var languages = rawStorageReader.repoLanguages(repo.getId());
        final var repoOwnerAccount = userIndexer.indexUser(repo.getOwner().getId());
        final var parentRepo = repo.getParent() != null ? indexRepo(repo.getParent().getId()) : null;
        return CleanRepo.of(
                repo,
                repoOwnerAccount,
                languages,
                parentRepo
        );
    }
}
