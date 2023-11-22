package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;

import java.time.Instant;
import java.util.Set;

public interface ContributionStorage {
    Set<Long> listReposWithContributionsUpdatedSince(Instant since);

    void saveAll(Contribution... contributions);
}
