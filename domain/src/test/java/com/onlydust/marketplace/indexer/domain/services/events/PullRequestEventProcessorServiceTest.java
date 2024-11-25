package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawPullRequestEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

class PullRequestEventProcessorServiceTest {
    private final Exposer<CleanRepo> repoExposer = mock(Exposer.class);
    private final RawStorageWriter rawStorageWriter = mock(RawStorageWriter.class);
    private final PullRequestIndexer pullRequestIndexer = mock(PullRequestIndexer.class);
    private final GithubAppContext githubAppContext = mock(GithubAppContext.class);
    private final GithubObserver githubObserver = mock(GithubObserver.class);
    private final PullRequestEventProcessorService eventProcessor = new PullRequestEventProcessorService(repoExposer, rawStorageWriter, pullRequestIndexer, githubAppContext, githubObserver);

    @BeforeEach
    void setUp() {
        reset(repoExposer, rawStorageWriter, pullRequestIndexer, githubAppContext, githubObserver);

        doAnswer(invocation -> {
            final var runnable = invocation.getArgument(1, Runnable.class);
            runnable.run();
            return null;
        }).when(githubAppContext).withGithubApp(any(Long.class), any(Runnable.class));
    }

    @Test
    void on_pull_request_event() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/pull_request/marketplace-frontend-pr-2305-opened.json", RawPullRequestEvent.class);
        final var pr = CleanPullRequest.of(event.getPullRequest());
        when(pullRequestIndexer.indexPullRequest(anyString(), anyString(), anyLong())).thenReturn(Optional.of(pr));

        // When
        eventProcessor.process(event);

        // Then
        verify(githubObserver).on(event);
        verify(rawStorageWriter).savePullRequest(event.getPullRequest());
        verify(pullRequestIndexer).indexPullRequest(event.getRepository().getOwner().getLogin(), event.getRepository().getName(), event.getPullRequest().getNumber());
        verify(repoExposer).expose(pr.getRepo());
    }
}