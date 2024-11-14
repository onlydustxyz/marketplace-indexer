package com.onlydust.marketplace.indexer.domain.services.observers;

import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawIssueCommentEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawPullRequestEvent;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import onlydust.com.marketplace.kernel.model.event.*;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

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
        assertThat(capturedEvent.repoId()).isEqualTo(699283256);
        assertThat(capturedEvent.assigneeId()).isEqualTo(43467246L);
        assertThat(capturedEvent.assignedById()).isEqualTo(43467246L);
        assertThat(capturedEvent.labels()).containsExactly("documentation", "good first issue");
        assertThat(capturedEvent.createdAt()).isEqualTo("2024-06-11T14:20:41Z");
        assertThat(capturedEvent.assignedAt()).isEqualTo("2024-06-11T14:20:55Z");
    }

    @Test
    void on_issue_deleted() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue/marketplace-indexer-issue-160-deleted.json", RawIssueEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        final var eventCaptor = ArgumentCaptor.forClass(OnGithubIssueDeleted.class);
        verify(outboxPort).push(eventCaptor.capture());
        final var capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.id()).isEqualTo(2346568062L);
    }

    @Test
    void on_issue_transferred() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue/marketplace-frontend-issue-78-transferred.json", RawIssueEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        final var eventCaptor = ArgumentCaptor.forClass(OnGithubIssueTransferred.class);
        verify(outboxPort).push(eventCaptor.capture());
        final var capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.id()).isEqualTo(1301824165L);
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
        assertThat(capturedEvent.repoId()).isEqualTo(498695724);
        assertThat(capturedEvent.authorId()).isEqualTo(17259618L);
        assertThat(capturedEvent.createdAt()).isEqualTo("2024-06-11T14:12:24Z");
    }

    @Test
    void on_pr_merged() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/pull_request/marketplace-frontend-pr-2305-closed.json", RawPullRequestEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        final var eventCaptor = ArgumentCaptor.forClass(OnPullRequestMerged.class);
        verify(outboxPort).push(eventCaptor.capture());
        final var capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.id()).isEqualTo(1914598578L);
        assertThat(capturedEvent.repoId()).isEqualTo(498695724);
        assertThat(capturedEvent.authorId()).isEqualTo(17259618L);
        assertThat(capturedEvent.createdAt()).isEqualTo("2024-06-11T14:12:24Z");
        assertThat(capturedEvent.mergedAt()).isEqualTo("2024-06-11T14:13:05Z");
    }

    @Test
    void on_issue_comment_created() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue_comment/marketplace-api-issue-812-comment-created.json", RawIssueCommentEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        final var eventCaptor = ArgumentCaptor.forClass(OnGithubCommentCreated.class);
        verify(outboxPort).push(eventCaptor.capture());
        final var capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.id()).isEqualTo(2172727402L);
        assertThat(capturedEvent.issueId()).isEqualTo(2356782090L);
        assertThat(capturedEvent.repoId()).isEqualTo(698096830);
        assertThat(capturedEvent.authorId()).isEqualTo(43467246);
        assertThat(capturedEvent.createdAt()).isEqualTo("2024-06-17T08:53:32Z");
        assertThat(capturedEvent.body()).isEqualTo("Hey I want to do this issue!");
    }


    @Test
    void on_issue_comment_created_by_bot() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue_comment/marketplace-api-issue-812-comment-created-by-bot.json",
                RawIssueCommentEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        verifyNoInteractions(outboxPort);
    }

    @Test
    void on_issue_comment_edited() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue_comment/marketplace-api-issue-812-comment-edited.json", RawIssueCommentEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        final var eventCaptor = ArgumentCaptor.forClass(OnGithubCommentEdited.class);
        verify(outboxPort).push(eventCaptor.capture());
        final var capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.id()).isEqualTo(2172727402L);
        assertThat(capturedEvent.issueId()).isEqualTo(2356782090L);
        assertThat(capturedEvent.repoId()).isEqualTo(698096830);
        assertThat(capturedEvent.authorId()).isEqualTo(43467246);
        assertThat(capturedEvent.updatedAt()).isEqualTo("2024-06-17T08:56:21Z");
        assertThat(capturedEvent.body()).isEqualTo("Hey I want this issue to be done!");
    }

    @Test
    void on_issue_comment_deleted() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/issue_comment/marketplace-api-issue-812-comment-deleted.json", RawIssueCommentEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        final var eventCaptor = ArgumentCaptor.forClass(OnGithubCommentDeleted.class);
        verify(outboxPort).push(eventCaptor.capture());
        final var capturedEvent = eventCaptor.getValue();
        assertThat(capturedEvent.id()).isEqualTo(2172727402L);
    }

    @Test
    void on_pull_request_comment_created() {
        // Given
        final var event = RawStorageWriterStub.load("/github/events/pr_comment/marketplace-frontend-pr-2335-comment-created.json",
                RawIssueCommentEvent.class);

        // When
        githubOutboxObserver.on(event);

        // Then
        verifyNoInteractions(outboxPort);
    }
}