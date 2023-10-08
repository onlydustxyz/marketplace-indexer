package com.onlydust.marketplace.indexer.postgres.entities;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(PullRequestClosingIssue.Id.class)
@Table(name = "pull_request_closing_issues", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PullRequestClosingIssue {
    @javax.persistence.Id
    @OneToOne
    PullRequest pullRequest;

    @javax.persistence.Id
    @OneToOne
    Issue issue;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

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

