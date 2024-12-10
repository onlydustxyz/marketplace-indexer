package com.onlydust.marketplace.indexer.domain.models.exposition;

import static com.onlydust.marketplace.indexer.domain.exception.OnlyDustException.internalServerError;

import java.util.Date;
import java.util.List;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import onlydust.com.marketplace.kernel.model.ContributionUUID;

@Value
@Builder(access = AccessLevel.PRIVATE)
public class GithubIssue {
    Long id;
    GithubRepo repo;
    Long number;
    String title;
    Status status;
    Date createdAt;
    Date updatedAt;
    Date closedAt;
    GithubAccount author;
    String htmlUrl;
    String body;
    Integer commentsCount;
    List<GithubLabel> labels;

    public static GithubIssue of(CleanIssue issue) {
        return GithubIssue.builder()
                .id(issue.getId())
                .repo(GithubRepo.of(issue.getRepo()))
                .number(issue.getNumber())
                .title(issue.getTitle())
                .status(Status.of(issue))
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .closedAt(issue.getClosedAt())
                .author(GithubAccount.of(issue.getAuthor()))
                .htmlUrl(issue.getHtmlUrl())
                .body(issue.getBody())
                .commentsCount(issue.getComments())
                .labels(issue.getLabels().stream().map(GithubLabel::of).toList())
                .build();
    }

    public ContributionUUID getContributionUUID() {
        return ContributionUUID.of(id);
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
                    return switch (issue.getStateReason()) {
                        case "not_planned" -> Status.CANCELLED;
                        case "completed" -> Status.COMPLETED;
                        case null -> Status.COMPLETED;
                        default -> throw internalServerError("Unknown issue state reason: " + issue.getStateReason());
                    };
            }
            throw internalServerError("Unknown issue state: " + issue.getState() + " " + issue.getStateReason());
        }

    }
}
