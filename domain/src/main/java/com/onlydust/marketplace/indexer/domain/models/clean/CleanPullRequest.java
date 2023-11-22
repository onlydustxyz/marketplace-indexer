package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@Value
public class CleanPullRequest {
    Long id;
    CleanRepo repo;
    Long number;
    String title;
    String state;
    Instant createdAt;
    Instant closedAt;
    Instant mergedAt;
    Boolean merged;
    CleanAccount author;
    String htmlUrl;
    String body;
    Integer comments;
    Boolean draft;
    @Builder.Default
    List<CleanCodeReview> reviews = new ArrayList<>();
    @Builder.Default
    List<CleanAccount> requestedReviewers = new ArrayList<>();
    @Builder.Default
    List<CleanCommit> commits = new ArrayList<>();
    @Builder.Default
    List<CleanCheckRun> checkRuns = new ArrayList<>();
    @Builder.Default
    List<CleanIssue> closingIssues = new ArrayList<>();

    public static CleanPullRequest of(RawPullRequest pullRequest, CleanRepo repo, CleanAccount author) {
        return CleanPullRequest.builder()
                .id(pullRequest.getId())
                .repo(repo)
                .number(pullRequest.getNumber())
                .title(pullRequest.getTitle())
                .state(pullRequest.getState())
                .createdAt(pullRequest.getCreatedAt())
                .closedAt(pullRequest.getClosedAt())
                .mergedAt(pullRequest.getMergedAt())
                .merged(pullRequest.getMerged())
                .htmlUrl(pullRequest.getHtmlUrl())
                .body(pullRequest.getBody())
                .comments(pullRequest.getComments())
                .author(author)
                .draft(pullRequest.getDraft())
                .build();
    }

    public static CleanPullRequest of(RawPullRequest pullRequest, CleanRepo repo, CleanAccount author, List<CleanCodeReview> reviews, List<CleanAccount> requestedReviewers, List<CleanCommit> commits, List<CleanCheckRun> checkRuns, List<CleanIssue> closingIssues) {
        return CleanPullRequest.of(pullRequest, repo, author).toBuilder()
                .reviews(reviews)
                .requestedReviewers(requestedReviewers)
                .commits(commits)
                .checkRuns(checkRuns)
                .closingIssues(closingIssues)
                .build();
    }
}
