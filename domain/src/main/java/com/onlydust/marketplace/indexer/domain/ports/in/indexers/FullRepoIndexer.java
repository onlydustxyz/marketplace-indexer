package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;

import java.util.Optional;

public interface FullRepoIndexer {
    Optional<CleanRepo> indexFullRepo(Long repoId);
}
