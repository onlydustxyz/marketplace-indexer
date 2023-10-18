package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
@Value
public class CleanPullRequest {
    Long id;
    CleanAccount author;
    List<CleanCodeReview> reviews;
    List<CleanAccount> requestedReviewers;
    List<CleanCommit> commits;
    List<CleanCheckRun> checkRuns;
    List<CleanIssue> closingIssues;

    public static CleanPullRequest of(RawPullRequest pullRequest, CleanAccount author, List<CleanCodeReview> reviews, List<CleanAccount> requestedReviewers, List<CleanCommit> commits, List<CleanCheckRun> checkRuns, List<CleanIssue> closingIssues) {
        return CleanPullRequest.builder()
                .id(pullRequest.getId())
                .author(author)
                .reviews(reviews)
                .requestedReviewers(requestedReviewers)
                .commits(commits)
                .checkRuns(checkRuns)
                .closingIssues(closingIssues)
                .build();
    }
}
