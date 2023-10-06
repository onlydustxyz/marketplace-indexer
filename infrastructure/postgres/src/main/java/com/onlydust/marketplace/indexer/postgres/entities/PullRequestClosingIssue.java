package com.onlydust.marketplace.indexer.postgres.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
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
    @Column(name = "pull_request_id")
    Long pullRequestId;
    @OneToOne(mappedBy = "pull_request_id")
    PullRequest pullRequest;
    @javax.persistence.Id
    @Column(name = "issue_id")
    Long issueId;
    @OneToOne(mappedBy = "issue_id")
    Issue issue;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    ZonedDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static PullRequestClosingIssue of(Long pullRequestId, Long issueId) {
        return PullRequestClosingIssue.builder().pullRequestId(pullRequestId).issueId(issueId).build();
    }

    @EqualsAndHashCode
    public static class Id implements Serializable {
        Long pullRequestId;
        Long issueId;
    }
}

