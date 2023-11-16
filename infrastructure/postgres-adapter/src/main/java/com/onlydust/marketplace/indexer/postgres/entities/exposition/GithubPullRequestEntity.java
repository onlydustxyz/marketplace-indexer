package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubPullRequest;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.Instant;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_pull_requests", schema = "indexer_exp")
@TypeDef(name = "github_pull_request_status", typeClass = PostgreSQLEnumType.class)
public class GithubPullRequestEntity {
    @Id
    Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    GithubRepoEntity repo;
    Long number;
    String title;
    @Enumerated(EnumType.STRING)
    @Type(type = "github_pull_request_status")
    Status status;
    Date createdAt;
    Date closedAt;
    Date mergedAt;
    String body;
    @ManyToOne(cascade = CascadeType.ALL)
    GithubAccountEntity author;
    String htmlUrl;
    Integer commentsCount;
    Boolean draft;
    String repoOwnerLogin;
    String repoName;
    String repoHtmlUrl;
    String authorLogin;
    String authorHtmlUrl;
    String authorAvatarUrl;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "github_pull_requests_closing_issues",
            schema = "indexer_exp",
            joinColumns = @JoinColumn(name = "pull_request_id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id"))
    Set<GithubIssueEntity> closingIssues;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    Instant techCreatedAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(nullable = false)
    Instant techUpdatedAt;

    public static GithubPullRequestEntity of(GithubPullRequest pullRequest) {
        return GithubPullRequestEntity.builder()
                .id(pullRequest.getId())
                .repo(GithubRepoEntity.of(pullRequest.getRepo()))
                .number(pullRequest.getNumber())
                .title(pullRequest.getTitle())
                .status(Status.of(pullRequest.getStatus()))
                .createdAt(pullRequest.getCreatedAt())
                .closedAt(pullRequest.getClosedAt())
                .mergedAt(pullRequest.getMergedAt())
                .body(pullRequest.getBody())
                .author(GithubAccountEntity.of(pullRequest.getAuthor()))
                .htmlUrl(pullRequest.getHtmlUrl())
                .commentsCount(pullRequest.getCommentsCount())
                .draft(pullRequest.getDraft())
                .repoOwnerLogin(pullRequest.getRepo().getOwner().getLogin())
                .repoName(pullRequest.getRepo().getName())
                .repoHtmlUrl(pullRequest.getRepo().getHtmlUrl())
                .authorLogin(pullRequest.getAuthor().getLogin())
                .authorHtmlUrl(pullRequest.getAuthor().getHtmlUrl())
                .authorAvatarUrl(pullRequest.getAuthor().getAvatarUrl())
                .closingIssues(pullRequest.getClosingIssues().stream().map(GithubIssueEntity::of).collect(Collectors.toUnmodifiableSet()))
                .build();
    }

    public enum Status {
        OPEN, CLOSED, MERGED;

        public static Status of(GithubPullRequest.Status status) {
            return switch (status) {
                case OPEN -> OPEN;
                case CLOSED -> CLOSED;
                case MERGED -> MERGED;
            };
        }
    }
}
