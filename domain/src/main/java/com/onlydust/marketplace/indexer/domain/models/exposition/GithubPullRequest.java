package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanCommit;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import lombok.Builder;
import lombok.Value;
import onlydust.com.marketplace.kernel.model.ContributionUUID;

import java.util.*;

import static com.onlydust.marketplace.indexer.domain.utils.FileUtils.fileExtension;
import static java.util.stream.Collectors.*;

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
    GithubAccount mergedBy;
    String htmlUrl;
    Integer commentsCount;
    Date createdAt;
    Date updatedAt;
    Date closedAt;
    Date mergedAt;
    String body;
    boolean draft;
    List<GithubIssue> closingIssues;
    ReviewState reviewState;
    Set<GithubCommit> commits;
    List<String> mainFileExtensions;

    public static GithubPullRequest of(CleanPullRequest pullRequest) {
        return GithubPullRequest.builder()
                .id(pullRequest.getId())
                .repo(GithubRepo.of(pullRequest.getRepo()))
                .number(pullRequest.getNumber())
                .title(pullRequest.getTitle())
                .status(Status.of(pullRequest))
                .author(GithubAccount.of(pullRequest.getAuthor()))
                .mergedBy(Optional.ofNullable(pullRequest.getMergedBy()).map(GithubAccount::of).orElse(null))
                .htmlUrl(pullRequest.getHtmlUrl())
                .commentsCount(pullRequest.getComments())
                .createdAt(pullRequest.getCreatedAt())
                .updatedAt(pullRequest.getUpdatedAt())
                .closedAt(pullRequest.getClosedAt())
                .mergedAt(pullRequest.getMergedAt())
                .body(pullRequest.getBody())
                .draft(pullRequest.isDraft())
                .closingIssues(pullRequest.getClosingIssues().stream().map(GithubIssue::of).toList())
                .reviewState(aggregateReviewState(pullRequest))
                .commits(pullRequest.getCommits().stream().map(GithubCommit::of).collect(toSet()))
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

    private static ReviewState aggregateReviewState(CleanPullRequest pullRequest) {
        if (pullRequest.getReviews().stream().anyMatch(review -> review.getState().equals("CHANGES_REQUESTED")))
            return ReviewState.CHANGES_REQUESTED;

        if (pullRequest.getReviews().stream().anyMatch(review -> review.getState().equals("APPROVED")))
            return ReviewState.APPROVED;

        return pullRequest.getReviews().isEmpty() && pullRequest.getRequestedReviewers().isEmpty() ? ReviewState.PENDING_REVIEWER : ReviewState.UNDER_REVIEW;
    }

    public ContributionUUID getContributionUUID() {
        return ContributionUUID.of(id);
    }

    public enum Status {
        OPEN,
        MERGED,
        CLOSED,
        DRAFT;

        public static Status of(CleanPullRequest pullRequest) {
            return switch (pullRequest.getState()) {
                case "open" -> pullRequest.isDraft() ? Status.DRAFT : Status.OPEN;
                case "closed" -> pullRequest.getMerged() ? Status.MERGED : Status.CLOSED;
                default -> throw new RuntimeException("Unknown pull request state: " + pullRequest.getState());
            };
        }
    }

    public enum ReviewState {
        PENDING_REVIEWER, UNDER_REVIEW, APPROVED, CHANGES_REQUESTED
    }
}
