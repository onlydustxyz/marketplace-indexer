package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import onlydust.com.marketplace.kernel.model.ContributionUUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class IssueEventProcessorServiceTest {
    private final IssueIndexer issueIndexer = mock(IssueIndexer.class);
    private final UserIndexer userIndexer = mock(UserIndexer.class);
    private final Exposer<CleanRepo> repoExposer = mock(Exposer.class);
    private final GithubAppContext githubAppContext = mock(GithubAppContext.class);
    private final RawStorageWriter rawStorageWriter = mock(RawStorageWriter.class);
    private final IssueStorage issueStorage = mock(IssueStorage.class);
    private final ContributionStorage contributionStorage = mock(ContributionStorage.class);
    private final GithubObserver githubObserver = mock(GithubObserver.class);
    private final IndexingObserver indexingObserver = mock(IndexingObserver.class);
    final IssueEventProcessorService issueEventProcessorService = new IssueEventProcessorService(issueIndexer, userIndexer, repoExposer, githubAppContext,
            rawStorageWriter, issueStorage, contributionStorage, githubObserver, indexingObserver);

    @Test
    void should_ignore_issue_that_is_pull_requests_event() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue/marketplace-frontend-issue-2484-edited.json", RawIssueEvent.class);

        // When
        issueEventProcessorService.process(event);

        // Then
        verify(rawStorageWriter, never()).deleteIssue(any());
        verify(issueStorage, never()).delete(any());
        verify(contributionStorage, never()).deleteAllByRepoIdAndGithubNumber(any(), any());
        verify(issueIndexer, never()).indexIssue(any(), any(), any());
        verify(githubObserver, never()).on(any(RawIssueEvent.class));
    }

    @Test
    void should_handle_issue_transfer_event() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue/marketplace-frontend-issue-78-transferred.json", RawIssueEvent.class);

        // When
        issueEventProcessorService.process(event);

        // Then
        verify(rawStorageWriter).deleteIssue(1301824165L);
        verify(issueStorage).delete(1301824165L);
        verify(contributionStorage).deleteAllByRepoIdAndGithubNumber(498695724L, 78L);
        verify(issueIndexer, never()).indexIssue(any(), any(), any());
        verify(githubObserver).on(event);
        verify(indexingObserver).onContributionsChanged(498695724L, ContributionUUID.of(1301824165L));
    }


    @Test
    void should_handle_issue_deleted_event() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue/marketplace-indexer-issue-160-deleted.json", RawIssueEvent.class);

        // When
        issueEventProcessorService.process(event);

        // Then
        verify(rawStorageWriter).deleteIssue(2346568062L);
        verify(issueStorage).delete(2346568062L);
        verify(contributionStorage).deleteAllByRepoIdAndGithubNumber(699283256L, 160L);
        verify(issueIndexer, never()).indexIssue(any(), any(), any());
        verify(githubObserver).on(event);
        verify(indexingObserver).onContributionsChanged(699283256L, ContributionUUID.of(2346568062L));
    }

    @Test
    void should_handle_issue_assigned_event() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue/marketplace-indexer-issue-160-assigned.json", RawIssueEvent.class);
        final var issue = RawStorageWriterStub.load("/github/repos/marketplace-indexer/issues/160.json", RawIssue.class);
        final var repo = RawStorageWriterStub.load("/github/repos/marketplace-indexer.json", RawRepo.class);
        final var assignee = RawStorageWriterStub.load("/github/users/anthony.json", RawAccount.class);
        final var sender = RawStorageWriterStub.load("/github/users/olivier.json", RawAccount.class);
        doAnswer(invocation -> {
            final var runnable = invocation.getArgument(1, Runnable.class);
            runnable.run();
            return null;
        }).when(githubAppContext).withGithubApp(any(Long.class), any(Runnable.class));
        when(issueIndexer.indexIssue(eq("onlydustxyz"), eq("marketplace-indexer"), eq(160L))).thenReturn(Optional.of(CleanIssue.of(issue, CleanRepo.of(repo))));
        when(userIndexer.indexUser(43467246L)).thenReturn(Optional.of(CleanAccount.of(assignee)));
        when(userIndexer.indexUser(595505L)).thenReturn(Optional.of(CleanAccount.of(sender)));

        // When
        issueEventProcessorService.process(event);

        // Then
        verify(githubObserver).on(event);
        verify(rawStorageWriter, never()).deleteIssue(any());
        verify(issueStorage, never()).delete(any());
        verify(contributionStorage, never()).deleteAllByRepoIdAndGithubNumber(any(), any());

        verify(issueIndexer).indexIssue(eq("onlydustxyz"), eq("marketplace-indexer"), eq(160L));
        verify(userIndexer).indexUser(43467246L);
        verify(userIndexer).indexUser(595505L);
        verify(repoExposer).expose(any());

        final var assigneeCaptor = ArgumentCaptor.forClass(GithubAccount.class);
        final var assignedByCaptor = ArgumentCaptor.forClass(GithubAccount.class);
        verify(issueStorage).saveAssignee(eq(issue.getId()), assigneeCaptor.capture(), assignedByCaptor.capture());
        assertThat(assigneeCaptor.getValue().getId()).isEqualTo(43467246L);
        assertThat(assignedByCaptor.getValue().getId()).isEqualTo(595505L);
        verify(indexingObserver).onContributionsChanged(699283256L, ContributionUUID.of(2346568062L));
    }
}