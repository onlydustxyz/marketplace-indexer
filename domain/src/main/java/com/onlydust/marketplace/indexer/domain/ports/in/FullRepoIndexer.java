package com.onlydust.marketplace.indexer.domain.ports.in;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;

public interface FullRepoIndexer {
    CleanRepo indexFullRepo(Long repoId);
}
