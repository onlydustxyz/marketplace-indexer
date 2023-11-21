package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class GithubPullRequest {
    Long id;
    GithubRepo repo;
    Long number;
    String title;
    Status status;
    GithubAccount author;
    String htmlUrl;
    Integer commentsCount;
    Date createdAt;
    Date closedAt;
    Date mergedAt;
    String body;
    Boolean draft;
    List<GithubIssue> closingIssues;
    ReviewState reviewState;
    Map<GithubAccount, Long> commitCounts;

    public static GithubPullRequest of(CleanPullRequest pullRequest) {
        return GithubPullRequest.builder()
                .id(pullRequest.getId())
                .repo(GithubRepo.of(pullRequest.getRepo()))
                .number(pullRequest.getNumber())
                .title(pullRequest.getTitle())
                .status(Status.of(pullRequest))
                .author(GithubAccount.of(pullRequest.getAuthor()))
                .htmlUrl(pullRequest.getHtmlUrl())
                .commentsCount(pullRequest.getComments())
                .createdAt(pullRequest.getCreatedAt())
                .closedAt(pullRequest.getClosedAt())
                .mergedAt(pullRequest.getMergedAt())
                .body(pullRequest.getBody())
                .draft(pullRequest.getDraft())
                .closingIssues(pullRequest.getClosingIssues().stream().map(GithubIssue::of).toList())
                .reviewState(aggregateReviewState(pullRequest))
                .commitCounts(pullRequest.getCommits().stream()
                        .collect(Collectors.groupingBy(c -> GithubAccount.of(c.getAuthor()), Collectors.counting()))
                )
                .build();
    }

    private static ReviewState aggregateReviewState(CleanPullRequest pullRequest) {
        if (pullRequest.getReviews().stream().anyMatch(review -> review.getState().equals("CHANGES_REQUESTED"))) {
            return ReviewState.CHANGES_REQUESTED;
        }
        if (pullRequest.getReviews().stream().anyMatch(review -> review.getState().equals("APPROVED"))) {
            return ReviewState.APPROVED;
        }
        return pullRequest.getReviews().isEmpty() && pullRequest.getRequestedReviewers().isEmpty() ? ReviewState.PENDING_REVIEWER : ReviewState.UNDER_REVIEW;
    }


    public enum Status {
        OPEN,
        MERGED,
        CLOSED,
        DRAFT;

        public static Status of(CleanPullRequest pullRequest) {
            switch (pullRequest.getState()) {
                case "open":
                    return pullRequest.getDraft() ? Status.DRAFT : Status.OPEN;
                case "closed":
                    return pullRequest.getMerged() ? Status.MERGED : Status.CLOSED;
            }
            throw new RuntimeException("Unknown pull request state: " + pullRequest.getState());
        }
    }

    public enum ReviewState {
        PENDING_REVIEWER, UNDER_REVIEW, APPROVED, CHANGES_REQUESTED
    }
}
