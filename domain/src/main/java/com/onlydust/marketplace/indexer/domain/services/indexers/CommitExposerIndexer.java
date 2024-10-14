package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.CommitIndexer;
import lombok.AllArgsConstructor;
import lombok.NonNull;

import java.util.Optional;

@AllArgsConstructor
public class CommitExposerIndexer implements CommitIndexer {
    CommitIndexer indexer;
    Exposer<CleanCommit> exposer;

    @Override
    public Optional<CleanCommit> indexCommit(@NonNull Long repoId, @NonNull String sha) {
        final var commit = indexer.indexCommit(repoId, sha);
        commit.ifPresent(exposer::expose);
        return commit;
    }
}
