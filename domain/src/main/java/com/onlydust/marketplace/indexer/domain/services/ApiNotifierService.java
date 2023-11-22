package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.ports.in.ApiNotifier;
import com.onlydust.marketplace.indexer.domain.ports.out.ApiClient;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ApiNotifierService implements ApiNotifier {
    private final ContributionStorage contributionStorage;
    private final ApiClient apiClient;

    @Override
    public void notifyUponNewContributions() {
        final var repoIds = contributionStorage.listReposWithContributionsUpdatedSince(null);
        if (!repoIds.isEmpty())
            apiClient.onNewContributions(repoIds);
    }
}
