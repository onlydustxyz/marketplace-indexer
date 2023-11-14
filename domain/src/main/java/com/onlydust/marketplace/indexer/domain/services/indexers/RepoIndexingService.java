package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawLanguages;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class RepoIndexingService implements RepoIndexer {
    private final RawStorageReader rawStorageReader;
    private final UserIndexer userIndexer;

    @Override
    public Optional<CleanRepo> indexRepo(Long repoId) {
        LOGGER.debug("Indexing repo {}", repoId);
        return rawStorageReader.repo(repoId)
                .flatMap(this::buildCleanRepo);
    }

    @Override
    public Optional<CleanRepo> indexRepo(String repoOwner, String repoName) {
        LOGGER.debug("Indexing repo {}/{}", repoOwner, repoName);
        return rawStorageReader.repo(repoOwner, repoName)
                .flatMap(this::buildCleanRepo);
    }

    private Optional<CleanRepo> buildCleanRepo(RawRepo repo) {
        return userIndexer.indexUser(repo.getOwner().getId()).map(repoOwnerAccount -> {
            final var languages = rawStorageReader.repoLanguages(repo.getId()).orElseGet(() -> {
                LOGGER.warn("Unable to fetch repo languages");
                return new RawLanguages();
            });

            final var parentRepo = Optional.ofNullable(repo.getParent()).flatMap(parent -> indexRepo(parent.getId()));
            return CleanRepo.of(
                    repo,
                    repoOwnerAccount,
                    languages,
                    parentRepo.orElse(null)
            );
        });
    }
}
