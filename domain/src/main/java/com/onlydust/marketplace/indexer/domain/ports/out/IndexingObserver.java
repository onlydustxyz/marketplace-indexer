package com.onlydust.marketplace.indexer.domain.ports.out;

public interface IndexingObserver {
    void onContributionsChanged(Long repoId);
}
