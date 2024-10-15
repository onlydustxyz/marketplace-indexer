package com.onlydust.marketplace.indexer.domain.services.indexers;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.CommitIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@AllArgsConstructor
@Slf4j
public class CommitIndexingService implements CommitIndexer {
    private final RawStorageReader rawStorageReader;
    private final UserIndexer userIndexer;

    @Override
    public Optional<CleanCommit> indexCommit(final @NonNull Long repoId, final @NonNull String sha) {
        LOGGER.debug("Indexing commit {}/{}", repoId, sha);
        return rawStorageReader.commit(repoId, sha)
                .filter(RawCommit::nonMerge)
                .map(CleanCommit::of)
                .map(commit -> commit.authorId().flatMap(userIndexer::indexUser).map(commit::withAuthor).orElse(commit));
    }
}
