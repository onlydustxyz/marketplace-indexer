package com.onlydust.marketplace.indexer.postgres.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.ZonedDateTime;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(PullRequestClosingIssue.Id.class)
@Table(name = "pull_request_closing_issues", schema = "indexer_raw")
public class PullRequestClosingIssue {
    @javax.persistence.Id
    @OneToOne
    PullRequest pullRequest;
    @javax.persistence.Id
    @OneToOne
    Issue issue;
    @CreationTimestamp
    ZonedDateTime createdAt;
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static PullRequestClosingIssue of(Long pullRequestId, Long issueId) {
        final var pullRequest = PullRequest.builder().id(pullRequestId).build();
        final var issue = Issue.builder().id(issueId).build();
        return PullRequestClosingIssue.builder().pullRequest(pullRequest).issue(issue).build();
    }

    @EqualsAndHashCode
    public static class Id implements Serializable {
        PullRequest pullRequest;
        Issue issue;
    }
}

