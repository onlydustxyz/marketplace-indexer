package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static java.util.stream.Collectors.toSet;

@Entity
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Table(name = "github_issues", schema = "indexer_exp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GithubIssueEntity {
    @Id
    Long id;

    UUID contributionUuid;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubRepoEntity repo;
    Long number;
    String title;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "github_issue_status")
    @JdbcType(PostgreSQLEnumJdbcType.class)
    Status status;
    Date createdAt;
    Date updatedAt;
    Date closedAt;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubAccountEntity author;
    String htmlUrl;
    String body;
    Integer commentsCount;
    String repoOwnerLogin;
    String repoName;
    String repoHtmlUrl;
    String authorLogin;
    String authorHtmlUrl;
    String authorAvatarUrl;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "github_issues_assignees",
            schema = "indexer_exp",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    Set<GithubAccountEntity> assignees;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "github_issues_labels",
            schema = "indexer_exp",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id"))
    Set<GithubLabelEntity> labels;

    public static GithubIssueEntity of(GithubIssue issue) {
        return GithubIssueEntity.builder()
                .id(issue.getId())
                .contributionUuid(issue.getContributionUUID().value())
                .repo(GithubRepoEntity.of(issue.getRepo()))
                .number(issue.getNumber())
                .title(issue.getTitle())
                .status(Status.of(issue.getStatus()))
                .createdAt(issue.getCreatedAt())
                .updatedAt(issue.getUpdatedAt())
                .closedAt(issue.getClosedAt())
                .author(GithubAccountEntity.of(issue.getAuthor()))
                .htmlUrl(issue.getHtmlUrl())
                .body(issue.getBody())
                .commentsCount(issue.getCommentsCount())
                .repoOwnerLogin(issue.getRepo().getOwner().getLogin())
                .repoName(issue.getRepo().getName())
                .repoHtmlUrl(issue.getRepo().getHtmlUrl())
                .authorLogin(issue.getAuthor().getLogin())
                .authorHtmlUrl(issue.getAuthor().getHtmlUrl())
                .authorAvatarUrl(issue.getAuthor().getAvatarUrl())
                .assignees(issue.getAssignees().stream().map(GithubAccountEntity::of).collect(toSet()))
                .labels(issue.getLabels().stream().map(GithubLabelEntity::of).collect(toSet()))
                .build();
    }

    public enum Status {
        OPEN, COMPLETED, CANCELLED;

        public static Status of(GithubIssue.Status status) {
            return switch (status) {
                case OPEN -> OPEN;
                case COMPLETED -> COMPLETED;
                case CANCELLED -> CANCELLED;
            };
        }
    }
}
