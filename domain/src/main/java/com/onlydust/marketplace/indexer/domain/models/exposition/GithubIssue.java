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
    String body;
    Integer commentsCount;
    List<GithubAccount> assignees;

    public static GithubIssue of(CleanIssue issue) {
        return GithubIssue.builder()
                .id(issue.getId())
                .repo(GithubRepo.of(issue.getRepo()))
                .number(issue.getNumber())
                .title(issue.getTitle())
                .status(Status.of(issue))
                .createdAt(issue.getCreatedAt())
                .closedAt(issue.getClosedAt())
                .author(GithubAccount.of(issue.getAuthor()))
                .htmlUrl(issue.getHtmlUrl())
                .body(issue.getBody())
                .commentsCount(issue.getComments())
                .assignees(issue.getAssignees().stream().map(GithubAccount::of).toList())
                .build();
    }

    public enum Status {
        OPEN,
        COMPLETED,
        CANCELLED;

        public static Status of(CleanIssue issue) {
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

    }
}
