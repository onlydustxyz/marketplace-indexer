package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import lombok.Builder;
import lombok.Value;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;

@Value
@Builder
public class GithubPullRequest {
    public static final double FILE_EXTENSION_RELEVANCE_THRESHOLD = 0.2;

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
    List<String> mainFileExtensions;

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
                        .collect(groupingBy(c -> GithubAccount.of(c.getAuthor()), Collectors.counting()))
                )
                .mainFileExtensions(extractMainFileExtensions(pullRequest.getCommits()))
                .build();
    }

    public static List<String> extractMainFileExtensions(List<CleanCommit> commits) {
        final var extensions = commits.stream()
                .flatMap(c -> c.getModifiedFiles().entrySet().stream())
                .map(e -> Map.entry(fileExtension(e.getKey()), e.getValue()))
                .filter(e -> e.getKey().isPresent())
                .collect(groupingBy(e -> e.getKey().get(), reducing(0, Map.Entry::getValue, Integer::sum)));

        final var total = extensions.values().stream().mapToInt(Integer::intValue).sum();
        return extensions.entrySet().stream()
                .filter(e -> e.getValue() / (double) total > FILE_EXTENSION_RELEVANCE_THRESHOLD)
                .map(Map.Entry::getKey)
                .toList();
    }

    private static Optional<String> fileExtension(String filePath) {
        final var fileName = new File(filePath).getName();
        return Optional.of(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(f.lastIndexOf('.') + 1));
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
