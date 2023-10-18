package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
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
    Integer comments;
    List<CleanCodeReview> reviews;
    List<CleanAccount> requestedReviewers;
    List<CleanCommit> commits;
    List<CleanCheckRun> checkRuns;
    List<CleanIssue> closingIssues;

    public static CleanPullRequest of(RawPullRequest pullRequest, CleanRepo repo, CleanAccount author, List<CleanCodeReview> reviews, List<CleanAccount> requestedReviewers, List<CleanCommit> commits, List<CleanCheckRun> checkRuns, List<CleanIssue> closingIssues) {
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
                .comments(pullRequest.getComments())
                .author(author)
                .reviews(reviews)
                .requestedReviewers(requestedReviewers)
                .commits(commits)
                .checkRuns(checkRuns)
                .closingIssues(closingIssues)
                .build();
    }
}
