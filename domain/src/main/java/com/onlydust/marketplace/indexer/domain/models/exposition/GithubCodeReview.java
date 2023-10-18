package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCodeReview;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class GithubCodeReview {
    GithubPullRequest pullRequest;
    GithubAccount author;
    State state;
    Date requestedAt;
    Date submittedAt;

    public static GithubCodeReview of(CleanCodeReview codeReview, CleanPullRequest pullRequest) {
        return GithubCodeReview.builder()
                .pullRequest(GithubPullRequest.of(pullRequest))
                .author(GithubAccount.of(codeReview.getAuthor()))
                .state(State.valueOf(codeReview.getState()))
                .requestedAt(pullRequest.getCreatedAt())
                .submittedAt(codeReview.getSubmittedAt())
                .build();
    }

    public enum State {
        PENDING,
        COMMENTED,
        APPROVED,
        CHANGES_REQUESTED,
        DISMISSED
    }
}
