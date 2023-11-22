package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.ports.out.ApiClient;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApiNotifierServiceTest {
    private final ContributionStorage contributionStorage = mock(ContributionStorage.class);
    private final ApiClient apiClient = mock(ApiClient.class);
    private final ApiNotifierService apiNotifierService = new ApiNotifierService(contributionStorage, apiClient);

    @Test
    public void should_notify_upon_new_contributions() {
        when(contributionStorage.listReposWithContributionsUpdatedSince(any())).thenReturn(Set.of(1L, 2L, 3L));
        apiNotifierService.notifyUponNewContributions();
        verify(apiClient).onNewContributions(Set.of(1L, 2L, 3L));
    }

    @Test
    public void should_not_notify_when_no_new_contributions() {
        when(contributionStorage.listReposWithContributionsUpdatedSince(any())).thenReturn(Set.of());
        apiNotifierService.notifyUponNewContributions();
        verify(apiClient, never()).onNewContributions(any());
    }
}