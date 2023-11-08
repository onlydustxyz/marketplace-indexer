package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;

public interface ContributionStorage {
    void saveAll(Contribution... contributions);
}
