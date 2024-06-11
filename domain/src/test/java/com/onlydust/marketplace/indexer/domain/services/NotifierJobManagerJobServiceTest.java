package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.jobs.NewContributionNotifierJob;
import com.onlydust.marketplace.indexer.domain.models.NewContributionsNotification;
import com.onlydust.marketplace.indexer.domain.models.NotifierJob;
import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.NotifierJobStorage;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class NotifierJobManagerJobServiceTest {
    private final ContributionStorage contributionStorage = mock(ContributionStorage.class);
    private final NotifierJobStorage notifierJobStorage = mock(NotifierJobStorage.class);
    private final IndexingObserver indexingObserver = mock(IndexingObserver.class);
    private final NewContributionNotifierJob notifierJob = new NewContributionNotifierJob(contributionStorage, indexingObserver, notifierJobStorage);

    @Test
    public void should_notify_upon_new_contributions() {
        final var previousNotificationOn = Instant.now().minusSeconds(30);
        final var newNotificationOn = Instant.now();

        when(notifierJobStorage.startJob()).thenReturn(new NotifierJob(1L, previousNotificationOn));
        when(contributionStorage.newContributionsNotification(previousNotificationOn))
                .thenReturn(new NewContributionsNotification(Set.of(1L, 2L, 3L), newNotificationOn));

        notifierJob.run();

        verify(indexingObserver).onNewContributions(Set.of(1L, 2L, 3L));
        verify(notifierJobStorage).endJob(new NotifierJob(1L, newNotificationOn));
    }

    @Test
    public void should_notify_all_contributions_on_first_run() {
        final var newNotificationOn = Instant.now();

        when(notifierJobStorage.startJob()).thenReturn(new NotifierJob(1L, null));
        when(contributionStorage.newContributionsNotification(Instant.EPOCH))
                .thenReturn(new NewContributionsNotification(Set.of(1L, 2L, 3L), newNotificationOn));

        notifierJob.run();

        verify(indexingObserver).onNewContributions(Set.of(1L, 2L, 3L));
        verify(notifierJobStorage).endJob(new NotifierJob(1L, newNotificationOn));
    }

    @Test
    public void should_not_notify_when_no_new_contributions() {
        when(contributionStorage.newContributionsNotification(any())).thenReturn(new NewContributionsNotification(Set.of(), null));
        notifierJob.run();
        verify(indexingObserver, never()).onNewContributions(any());
    }
}