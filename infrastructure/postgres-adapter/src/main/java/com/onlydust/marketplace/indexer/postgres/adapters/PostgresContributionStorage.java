package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.Set;

@AllArgsConstructor
public class PostgresContributionStorage implements ContributionStorage {
    private final ContributionRepository contributionRepository;

    @Override
    public Set<Long> listReposWithContributionsUpdatedSince(Instant since) {
        return contributionRepository.listReposWithContributionsUpdatedSince(since);
    }

    @Override
    public void saveAll(Contribution... contributions) {
        contributionRepository.saveAll(Arrays.stream(contributions).map(ContributionEntity::of).toList());
    }
}
