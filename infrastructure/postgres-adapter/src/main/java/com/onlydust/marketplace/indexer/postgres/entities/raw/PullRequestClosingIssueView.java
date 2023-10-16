package com.onlydust.marketplace.indexer.postgres.entities.raw;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@IdClass(PullRequestClosingIssueView.Id.class)
@Table(name = "pull_request_closing_issues", schema = "indexer_raw")
public class PullRequestClosingIssueView {
    @javax.persistence.Id
    @ManyToOne
    @JoinColumn(name = "pull_request_id")
    PullRequest pullRequest;

    @javax.persistence.Id
    @ManyToOne
    @JoinColumn(name = "issue_id")
    Issue issue;

    @CreationTimestamp
    @EqualsAndHashCode.Exclude
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @UpdateTimestamp
    @EqualsAndHashCode.Exclude
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Id implements Serializable {
        Long pullRequest;
        Long issue;
    }
}

