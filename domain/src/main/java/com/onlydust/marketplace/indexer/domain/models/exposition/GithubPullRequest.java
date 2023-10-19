package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.List;

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
    List<GithubIssue> closingIssues;

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
                .closingIssues(pullRequest.getClosingIssues().stream().map(GithubIssue::of).toList())
                .build();
    }


    public enum Status {
        OPEN,
        MERGED,
        CLOSED;

        public static Status of(CleanPullRequest pullRequest) {
            switch (pullRequest.getState()) {
                case "open":
                    return Status.OPEN;
                case "closed":
                    return pullRequest.getMerged() ? Status.MERGED : Status.CLOSED;
            }
            throw new RuntimeException("Unknown pull request state: " + pullRequest.getState());
        }
    }
}
