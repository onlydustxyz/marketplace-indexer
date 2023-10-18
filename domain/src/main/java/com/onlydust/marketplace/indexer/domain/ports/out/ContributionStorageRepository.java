package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;

public interface ContributionStorageRepository {
    void saveAll(Contribution... contributions);
}
