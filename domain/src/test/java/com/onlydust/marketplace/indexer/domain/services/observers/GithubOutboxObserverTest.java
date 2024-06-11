package com.onlydust.marketplace.indexer.domain.services.observers;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestEvent;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import onlydust.com.marketplace.kernel.model.event.OnGithubIssueAssigned;
import onlydust.com.marketplace.kernel.model.event.OnPullRequestCreated;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class GithubOutboxObserverTest {
    private final OutboxPort outboxPort = mock(OutboxPort.class);
    private final GithubOutboxObserver githubOutboxObserver = new GithubOutboxObserver(outboxPort);

    @Test
    void on_issue_assigned() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue/marketplace-indexer-issue-160-assigned.json", RawIssueEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        final var eventCaptor = ArgumentCaptor.forClass(OnGithubIssueAssigned.class);
        verify(outboxPort).push(eventCaptor.capture());
        final var capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.id()).isEqualTo(2346568062L);
        assertThat(capturedEvent.assigneeId()).isEqualTo(43467246L);
        assertThat(capturedEvent.labels()).containsExactly("documentation", "good first issue");
        assertThat(capturedEvent.assignedAt()).isEqualTo("2024-06-11T14:20:55Z");
    }

    @Test
    void on_pr_created() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/pull_request/marketplace-frontend-pr-2305-opened.json", RawPullRequestEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        final var eventCaptor = ArgumentCaptor.forClass(OnPullRequestCreated.class);
        verify(outboxPort).push(eventCaptor.capture());
        final var capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.id()).isEqualTo(1914598578L);
        assertThat(capturedEvent.authorId()).isEqualTo(17259618L);
        assertThat(capturedEvent.createdAt()).isEqualTo("2024-06-11T14:12:24Z");
    }
}