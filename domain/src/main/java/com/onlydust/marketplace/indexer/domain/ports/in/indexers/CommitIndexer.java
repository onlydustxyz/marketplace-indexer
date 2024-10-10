package com.onlydust.marketplace.indexer.domain.ports.in.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import lombok.NonNull;

import java.util.Optional;

public interface CommitIndexer {
    Optional<CleanCommit> indexCommit(final @NonNull Long repoId, final @NonNull String sha);
}
