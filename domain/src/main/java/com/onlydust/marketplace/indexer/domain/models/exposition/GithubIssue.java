package com.onlydust.marketplace.indexer.domain.models.exposition;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class GithubIssue {
    Long id;
    GithubRepo repo;
    Long number;
    String title;
    Status status;
    Date createdAt;
    Date closedAt;
    GithubAccount author;
    String htmlUrl;
    Integer commentsCount;
    List<GithubAccount> assignees;

    public static GithubIssue of(CleanIssue issue) {
        return GithubIssue.builder()
                .id(issue.getId())
                .repo(GithubRepo.of(issue.getRepo()))
                .number(issue.getNumber())
                .title(issue.getTitle())
                .status(buildStatus(issue))
                .createdAt(issue.getCreatedAt())
                .closedAt(issue.getClosedAt())
                .author(GithubAccount.of(issue.getAuthor()))
                .htmlUrl(issue.getHtmlUrl())
                .commentsCount(issue.getComments())
                .build();
    }

    private static Status buildStatus(CleanIssue issue) {
        switch (issue.getState()) {
            case "open":
                return Status.OPEN;
            case "closed":
                switch (issue.getStateReason()) {
                    case "completed":
                        return Status.COMPLETED;
                    case "not_planned":
                        return Status.CANCELLED;
                }
        }
        throw OnlyDustException.internalServerError("Unknown issue state: " + issue.getState() + " " + issue.getStateReason());
    }

    public enum Status {
        OPEN,
        COMPLETED,
        CANCELLED
    }
}
