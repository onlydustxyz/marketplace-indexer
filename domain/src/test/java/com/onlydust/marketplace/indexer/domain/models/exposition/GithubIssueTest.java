package com.onlydust.marketplace.indexer.domain.models.exposition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;

class GithubIssueTest {

    @Test
    void should_map_issue_status() {
        // Given
        final var openIssue = mock(CleanIssue.class);
        when(openIssue.getState()).thenReturn("open");

        final var completedIssue = mock(CleanIssue.class);
        when(completedIssue.getState()).thenReturn("closed");
        when(completedIssue.getStateReason()).thenReturn("completed");

        final var nullReasonClosedIssue = mock(CleanIssue.class);
        when(nullReasonClosedIssue.getState()).thenReturn("closed");
        when(nullReasonClosedIssue.getStateReason()).thenReturn(null);

        final var cancelledIssue = mock(CleanIssue.class);
        when(cancelledIssue.getState()).thenReturn("closed");
        when(cancelledIssue.getStateReason()).thenReturn("not_planned");

        final var unknownStateIssue = mock(CleanIssue.class);
        when(unknownStateIssue.getState()).thenReturn("unknown");

        final var unknownReasonIssue = mock(CleanIssue.class);
        when(unknownReasonIssue.getState()).thenReturn("closed");
        when(unknownReasonIssue.getStateReason()).thenReturn("unknown_reason");

        // When & Then
        assertThat(GithubIssue.Status.of(openIssue)).isEqualTo(GithubIssue.Status.OPEN);
        assertThat(GithubIssue.Status.of(completedIssue)).isEqualTo(GithubIssue.Status.COMPLETED);
        assertThat(GithubIssue.Status.of(nullReasonClosedIssue)).isEqualTo(GithubIssue.Status.COMPLETED);
        assertThat(GithubIssue.Status.of(cancelledIssue)).isEqualTo(GithubIssue.Status.CANCELLED);

        assertThatThrownBy(() -> GithubIssue.Status.of(unknownStateIssue))
                .isInstanceOf(OnlyDustException.class)
                .hasMessageContaining("Unknown issue state: unknown null");

        assertThatThrownBy(() -> GithubIssue.Status.of(unknownReasonIssue))
                .isInstanceOf(OnlyDustException.class)
                .hasMessageContaining("Unknown issue state reason: unknown_reason");
    }
} 