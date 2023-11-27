package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.NewContributionsNotification;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionNotificationEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Arrays;

@AllArgsConstructor
public class PostgresContributionStorage implements ContributionStorage {
    private final ContributionRepository contributionRepository;
    private final ContributionNotificationEntityRepository contributionNotificationEntityRepository;

    @Override
    public NewContributionsNotification newContributionsNotification(Instant since) {
        final var result = contributionNotificationEntityRepository.listReposWithContributionsUpdatedSince(since);
        return new NewContributionsNotification(result.getRepoIds(), result.getLastUpdatedAt());
    }

    @Override
    public void saveAll(Contribution... contributions) {
        contributionRepository.saveAll(Arrays.stream(contributions).map(ContributionEntity::of).toList());
    }

    @Override
    public void deleteAllByRepoIdAndGithubNumber(Long id, Long number) {
        contributionRepository.deleteAllByRepoIdAndGithubNumber(id, number);
    }
}
