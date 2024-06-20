package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class IssueEventProcessorServiceTest {
    private final IssueIndexer issueIndexer = mock(IssueIndexer.class);
    private final Exposer<CleanRepo> repoExposer = mock(Exposer.class);
    private final GithubAppContext githubAppContext = mock(GithubAppContext.class);
    private final RawStorageWriter rawStorageWriter = mock(RawStorageWriter.class);
    private final IssueStorage issueStorage = mock(IssueStorage.class);
    private final ContributionStorage contributionStorage = mock(ContributionStorage.class);
    private final GithubObserver githubObserver = mock(GithubObserver.class);
    final IssueEventProcessorService issueEventProcessorService = new IssueEventProcessorService(issueIndexer, repoExposer, githubAppContext, rawStorageWriter
            , issueStorage, contributionStorage, githubObserver);

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
    }
}