package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@Value
public class CleanPullRequest {
    Long id;
    CleanRepo repo;
    Long number;
    String title;
    String state;
    Date createdAt;
    Date closedAt;
    Date mergedAt;
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
    List<CleanIssue> closingIssues = new ArrayList<>();

    public static CleanPullRequest of(RawPullRequest pullRequest) {
        return CleanPullRequest.builder()
                .id(pullRequest.getId())
                .repo(CleanRepo.of(pullRequest.getHead().getRepo()))
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
                .author(CleanAccount.of(pullRequest.getAuthor()))
                .draft(pullRequest.getDraft())
                .build();
    }

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

    public static CleanPullRequest of(RawPullRequest pullRequest, CleanRepo repo, CleanAccount author, List<CleanCodeReview> reviews,
                                      List<CleanAccount> requestedReviewers, List<CleanCommit> commits, List<CleanIssue> closingIssues) {
        return CleanPullRequest.of(pullRequest, repo, author).toBuilder()
                .reviews(reviews)
                .requestedReviewers(requestedReviewers)
                .commits(commits)
                .closingIssues(closingIssues)
                .build();
    }
}
