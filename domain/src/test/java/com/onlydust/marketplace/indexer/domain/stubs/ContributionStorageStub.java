package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;

import java.time.Instant;
import java.util.*;

public class ContributionStorageStub implements ContributionStorage {
    final Map<String, Contribution> contributions = new HashMap<>();

    @Override
    public Set<Long> listReposWithContributionsUpdatedSince(Instant since) {
        return Set.of();
    }

    @Override
    public void saveAll(Contribution... contributions) {
        Arrays.stream(contributions).forEach(c -> this.contributions.put(c.getId(), c));
    }

    public List<Contribution> contributions() {
        return contributions.values().stream().toList();
    }
}
