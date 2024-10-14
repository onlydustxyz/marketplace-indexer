package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCommit;
import lombok.NonNull;

public interface CommitStorage {

    void save(final @NonNull GithubCommit commit);
}
