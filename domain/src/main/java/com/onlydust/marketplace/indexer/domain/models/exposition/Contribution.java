package com.onlydust.marketplace.indexer.domain.models.exposition;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class Contribution {
    GithubRepo repo;
    GithubAccount contributor;
    Type type;
    Status status;
    GithubPullRequest pullRequest;
    GithubIssue issue;
    GithubCodeReview codeReview;
    Date createdAt;
    Date completedAt;

    public static Contribution of(GithubPullRequest pullRequest) {
        return Contribution.builder()
                .repo(pullRequest.getRepo())
                .contributor(pullRequest.getAuthor())
                .type(Type.PULL_REQUEST)
                .status(buildStatus(pullRequest.getStatus()))
                .pullRequest(pullRequest)
                .createdAt(pullRequest.getCreatedAt())
                .completedAt(pullRequest.getClosedAt())
                .build();
    }

    public static Contribution of(GithubIssue issue) {
        return Contribution.builder()
                .repo(issue.getRepo())
                .contributor(issue.getAuthor())
                .type(Type.ISSUE)
                .status(buildStatus(issue.getStatus()))
                .issue(issue)
                .createdAt(issue.getCreatedAt())
                .completedAt(issue.getClosedAt())
                .build();
    }

    public static Contribution of(GithubCodeReview codeReview) {
        final var status = buildStatus(codeReview.getState());
        return Contribution.builder()
                .repo(codeReview.getPullRequest().getRepo())
                .contributor(codeReview.getAuthor())
                .type(Type.CODE_REVIEW)
                .status(status)
                .codeReview(codeReview)
                .createdAt(codeReview.getRequestedAt())
                .completedAt(status == Status.IN_PROGRESS ? null : codeReview.getSubmittedAt())
                .build();
    }

    private static Status buildStatus(GithubPullRequest.Status status) {
        return switch (status) {
            case OPEN -> Status.IN_PROGRESS;
            case CLOSED -> Status.CANCELLED;
            case MERGED -> Status.COMPLETED;
        };
    }

    private static Status buildStatus(GithubIssue.Status status) {
        return switch (status) {
            case OPEN -> Status.IN_PROGRESS;
            case CANCELLED -> Status.CANCELLED;
            case COMPLETED -> Status.COMPLETED;
        };
    }

    private static Status buildStatus(GithubCodeReview.State state) {
        return switch (state) {
            case PENDING, COMMENTED -> Status.IN_PROGRESS;
            case APPROVED, CHANGES_REQUESTED -> Status.COMPLETED;
            case DISMISSED -> Status.CANCELLED;
        };
    }

    public enum Type {
        ISSUE,
        PULL_REQUEST,
        CODE_REVIEW
    }

    public enum Status {
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }
}
