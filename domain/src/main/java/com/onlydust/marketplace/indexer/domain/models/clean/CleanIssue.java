package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
@Value
public class CleanIssue {
    Long id;
    CleanRepo repo;
    Long number;
    String title;
    String state;
    String stateReason;
    Date createdAt;
    Date closedAt;
    CleanAccount author;
    String htmlUrl;
    String body;
    List<CleanAccount> assignees;
    Integer comments;

    public static CleanIssue of(RawIssue issue, CleanRepo repo, CleanAccount author, List<CleanAccount> assignees) {
        return CleanIssue.builder()
                .id(issue.getId())
                .repo(repo)
                .number(issue.getNumber())
                .title(issue.getTitle())
                .state(issue.getState())
                .stateReason(issue.getStateReason())
                .createdAt(issue.getCreatedAt())
                .closedAt(issue.getClosedAt())
                .author(author)
                .htmlUrl(issue.getHtmlUrl())
                .body(issue.getBody())
                .assignees(assignees)
                .comments(issue.getComments())
                .build();
    }

    public static CleanIssue of(RawIssue issue, CleanRepo repo) {
        return CleanIssue.of(issue, repo, CleanAccount.of(issue.getAuthor()), issue.getAssignees().stream().map(CleanAccount::of).toList());
    }
}
