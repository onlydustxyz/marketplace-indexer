package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;

import java.util.Set;

public interface IndexingObserver {
    void onNewContributions(Set<Long> repoIds);

    void onNewContributions(Contribution... contributions);
}
