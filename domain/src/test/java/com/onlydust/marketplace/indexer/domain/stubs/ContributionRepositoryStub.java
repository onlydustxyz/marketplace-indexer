package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.ports.out.ContributionStorageRepository;

import java.util.ArrayList;
import java.util.List;

public class ContributionRepositoryStub implements ContributionStorageRepository {
    final List<Contribution> contributions = new ArrayList<>();

    @Override
    public void saveAll(Contribution... contributions) {
        this.contributions.addAll(List.of(contributions));
    }

    public List<Contribution> contributions() {
        return contributions;
    }
}
