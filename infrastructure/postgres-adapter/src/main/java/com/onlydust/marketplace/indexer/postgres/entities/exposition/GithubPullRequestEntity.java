package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubPullRequest;
import io.hypersistence.utils.hibernate.type.basic.PostgreSQLEnumType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

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

    @ManyToOne
    GithubRepoEntity repo;
    Long number;
    String title;
    @Enumerated(EnumType.STRING)
    @Type(type = "github_pull_request_status")
    Status status;
    Date createdAt;
    Date closedAt;
    Date mergedAt;
    @ManyToOne
    GithubAccountEntity author;
    String htmlUrl;
    Integer commentsCount;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "github_pull_requests_closing_issues",
            joinColumns = @JoinColumn(name = "pull_request_id"),
            inverseJoinColumns = @JoinColumn(name = "issue_id"))
    List<GithubIssueEntity> closingIssues;

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
                .author(GithubAccountEntity.of(pullRequest.getAuthor()))
                .htmlUrl(pullRequest.getHtmlUrl())
                .commentsCount(pullRequest.getCommentsCount())
                .closingIssues(pullRequest.getClosingIssues().stream().map(GithubIssueEntity::of).toList())
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
