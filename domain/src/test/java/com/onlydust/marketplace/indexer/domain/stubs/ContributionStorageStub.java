package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.NewContributionsNotification;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContributionStorageStub implements ContributionStorage {
    final Map<String, Contribution> contributions = new HashMap<>();

    @Override
    public NewContributionsNotification newContributionsNotification(Instant since) {
        return null;
    }

    @Override
    public void saveAll(Contribution... contributions) {
        Arrays.stream(contributions).forEach(c -> this.contributions.put(c.getId(), c));
    }

    public List<Contribution> contributions() {
        return contributions.values().stream().toList();
    }
}
