package com.onlydust.marketplace.indexer.postgres.adapters;

import com.onlydust.marketplace.indexer.domain.models.NewContributionsNotification;
import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.ContributionEntity;
import com.onlydust.marketplace.indexer.postgres.entities.exposition.GroupedContributionEntity;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionNotificationEntityRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.ContributionRepository;
import com.onlydust.marketplace.indexer.postgres.repositories.exposition.GroupedContributionRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

import java.time.Instant;
import java.util.Arrays;
import java.util.stream.Stream;

@AllArgsConstructor
public class PostgresContributionStorage implements ContributionStorage {
    private final ContributionRepository contributionRepository;
    private final GroupedContributionRepository groupedContributionRepository;
    private final ContributionNotificationEntityRepository contributionNotificationEntityRepository;

    @Override
    public NewContributionsNotification newContributionsNotification(Instant since) {
        final var result = contributionNotificationEntityRepository.listReposWithContributionsUpdatedSince(since);
        return new NewContributionsNotification(result.getRepoIds(), result.getLastUpdatedAt());
    }

    @Override
    @Transactional
    public void saveAll(Contribution... contributions) {
        contributionRepository.mergeAll(Arrays.stream(contributions).map(ContributionEntity::of).toList());
        groupedContributionRepository.mergeAll(GroupedContributionEntity.of(Stream.of(contributions)));
    }

    @Override
    public void deleteAllByRepoIdAndGithubNumber(Long id, Long number) {
        contributionRepository.deleteAllByRepoIdAndGithubNumber(id, number);
        groupedContributionRepository.deleteAllByRepoIdAndGithubNumber(id, number);
    }
}
