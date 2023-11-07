package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;

@Entity
@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "contributions", schema = "indexer_exp")
@TypeDef(name = "contribution_type", typeClass = PostgreSQLEnumType.class)
@TypeDef(name = "contribution_status", typeClass = PostgreSQLEnumType.class)
public class ContributionEntity {
    @Id
    String id;
    @ManyToOne(cascade = CascadeType.ALL)
    GithubRepoEntity repo;
    @ManyToOne(cascade = CascadeType.ALL)
    GithubAccountEntity contributor;
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.Type(type = "contribution_type")
    Type type;
    @Enumerated(EnumType.STRING)
    @org.hibernate.annotations.Type(type = "contribution_status")
    Status status;
    @ManyToOne(cascade = CascadeType.ALL)
    GithubPullRequestEntity pullRequest;
    @ManyToOne(cascade = CascadeType.ALL)
    GithubIssueEntity issue;
    @ManyToOne(cascade = CascadeType.ALL)
    GithubCodeReviewEntity codeReview;
    Date createdAt;
    Date completedAt;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant techCreatedAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(nullable = false)
    Instant techUpdatedAt;

    public static ContributionEntity of(Contribution contribution) {
        return ContributionEntity.builder()
                .id(contribution.getId())
                .repo(GithubRepoEntity.of(contribution.getRepo()))
                .contributor(GithubAccountEntity.of(contribution.getContributor()))
                .type(Type.of(contribution.getType()))
                .status(Status.of(contribution.getStatus()))
                .pullRequest(contribution.getPullRequest() != null ? GithubPullRequestEntity.of(contribution.getPullRequest()) : null)
                .issue(contribution.getIssue() != null ? GithubIssueEntity.of(contribution.getIssue()) : null)
                .codeReview(contribution.getCodeReview() != null ? GithubCodeReviewEntity.of(contribution.getCodeReview()) : null)
                .createdAt(contribution.getCreatedAt())
                .completedAt(contribution.getCompletedAt())
                .build();
    }

    public enum Type {
        PULL_REQUEST, ISSUE, CODE_REVIEW;

        public static Type of(Contribution.Type type) {
            return switch (type) {
                case PULL_REQUEST -> PULL_REQUEST;
                case ISSUE -> ISSUE;
                case CODE_REVIEW -> CODE_REVIEW;
            };
        }
    }

    public enum Status {
        IN_PROGRESS, COMPLETED, CANCELLED;

        public static Status of(Contribution.Status status) {
            return switch (status) {
                case IN_PROGRESS -> IN_PROGRESS;
                case COMPLETED -> COMPLETED;
                case CANCELLED -> CANCELLED;
            };
        }
    }
}
