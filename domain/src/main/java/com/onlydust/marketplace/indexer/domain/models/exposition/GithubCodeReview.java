package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanCodeReview;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.utils.ContributionUUID;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Value
@Builder
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

    public static GithubCodeReview of(CleanAccount reviewer, CleanPullRequest pullRequest) {
        return GithubCodeReview.builder()
                .pullRequest(GithubPullRequest.of(pullRequest))
                .author(GithubAccount.of(reviewer))
                .state(State.PENDING)
                .requestedAt(pullRequest.getCreatedAt())
                .build();
    }

    public String getId() {
        return sha256Hex(String.format("(%d,%d)", pullRequest.getId(), author.getId()));
    }

    public ContributionUUID getContributionUUID() {
        return ContributionUUID.of(getId());
    }

    public Date getUpdatedAt() {
        return submittedAt != null ? submittedAt : requestedAt;
    }

    public enum State {
        PENDING,
        COMMENTED,
        APPROVED,
        CHANGES_REQUESTED,
        DISMISSED;

        public boolean isCompleted() {
            return this == APPROVED || this == CHANGES_REQUESTED || this == DISMISSED;
        }
    }
}
