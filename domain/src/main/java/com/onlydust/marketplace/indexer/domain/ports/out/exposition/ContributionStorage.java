package com.onlydust.marketplace.indexer.domain.ports.out.exposition;

import com.onlydust.marketplace.indexer.domain.models.NewContributionsNotification;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;

import java.time.Instant;

public interface ContributionStorage {
    NewContributionsNotification newContributionsNotification(Instant since);

    void saveAll(Contribution... contributions);
}
