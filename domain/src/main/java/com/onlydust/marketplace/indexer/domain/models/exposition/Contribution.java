package com.onlydust.marketplace.indexer.domain.models.exposition;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;

import static org.apache.commons.codec.digest.DigestUtils.sha256Hex;

@Value
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
public class Contribution {
    GithubRepo repo;
    GithubAccount contributor;
    Type type;
    Status status;
    GithubPullRequest pullRequest;
    GithubIssue issue;
    GithubCodeReview codeReview;
    Instant createdAt;
    Instant completedAt;
    GithubPullRequest.ReviewState pullRequestReviewState;

    public static Contribution of(GithubPullRequest pullRequest) {
        return Contribution.builder()
                .repo(pullRequest.getRepo())
                .contributor(pullRequest.getAuthor())
                .type(Type.PULL_REQUEST)
                .status(Status.of(pullRequest.getStatus()))
                .pullRequest(pullRequest)
                .createdAt(pullRequest.getCreatedAt())
                .completedAt(pullRequest.getClosedAt())
                .pullRequestReviewState(pullRequest.getReviewState())
                .build();
    }

    public static Contribution of(GithubIssue issue, GithubAccount assignee) {
        return Contribution.builder()
                .repo(issue.getRepo())
                .contributor(assignee)
                .type(Type.ISSUE)
                .status(Status.of(issue.getStatus()))
                .issue(issue)
                .createdAt(issue.getCreatedAt())
                .completedAt(issue.getClosedAt())
                .build();
    }

    public static Contribution of(GithubCodeReview codeReview) {
        final var status = Status.of(codeReview.getState(), codeReview.getPullRequest().getStatus());
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

    public static Contribution of(GithubCommit commit) {
        return Contribution.of(commit.getPullRequest()).toBuilder()
                .contributor(commit.getAuthor())
                .build();
    }

    public String getId() {
        return sha256Hex(String.format("(%s,%s,%d)", type, getDetailsId(), contributor.getId()));
    }

    private String getDetailsId() {
        return switch (type) {
            case PULL_REQUEST -> pullRequest.getId().toString();
            case ISSUE -> issue.getId().toString();
            case CODE_REVIEW -> codeReview.getId();
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
        CANCELLED;


        public static Status of(GithubPullRequest.Status status) {
            return switch (status) {
                case OPEN, DRAFT -> Status.IN_PROGRESS;
                case CLOSED -> Status.CANCELLED;
                case MERGED -> Status.COMPLETED;
            };
        }

        public static Status of(GithubIssue.Status status) {
            return switch (status) {
                case OPEN -> Status.IN_PROGRESS;
                case CANCELLED -> Status.CANCELLED;
                case COMPLETED -> Status.COMPLETED;
            };
        }

        public static Status of(GithubCodeReview.State codeReviewState, GithubPullRequest.Status pullRequestStatus) {
            return switch (codeReviewState) {
                case PENDING, COMMENTED -> switch (pullRequestStatus) {
                    case CLOSED, MERGED -> Status.CANCELLED;
                    default -> Status.IN_PROGRESS;
                };
                case APPROVED, CHANGES_REQUESTED -> Status.COMPLETED;
                case DISMISSED -> Status.CANCELLED;
            };
        }

    }
}
