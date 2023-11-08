package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public class PostgresContributionStorage implements ContributionStorage {
    private final ContributionRepository contributionRepository;

    @Override
    public void saveAll(Contribution... contributions) {
        contributionRepository.saveAll(Arrays.stream(contributions).map(ContributionEntity::of).toList());
    }
}
