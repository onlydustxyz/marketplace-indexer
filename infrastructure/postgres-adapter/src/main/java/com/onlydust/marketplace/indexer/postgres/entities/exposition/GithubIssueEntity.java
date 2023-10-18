package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubIssue;
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
@Table(name = "github_issues", schema = "indexer_exp")
@TypeDef(name = "github_issue_status", typeClass = PostgreSQLEnumType.class)
public class GithubIssueEntity {
    @Id
    Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    GithubRepoEntity repo;
    Long number;
    String title;
    @Enumerated(EnumType.STRING)
    @Type(type = "github_issue_status")
    Status status;
    Date createdAt;
    Date closedAt;
    @ManyToOne(cascade = CascadeType.ALL)
    GithubAccountEntity author;
    String htmlUrl;
    String body;
    Integer commentsCount;
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "github_issues_assignees",
            schema = "indexer_exp",
            joinColumns = @JoinColumn(name = "issue_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    List<GithubAccountEntity> assignees;

    public static GithubIssueEntity of(GithubIssue issue) {
        return GithubIssueEntity.builder()
                .id(issue.getId())
                .repo(GithubRepoEntity.of(issue.getRepo()))
                .number(issue.getNumber())
                .title(issue.getTitle())
                .status(Status.of(issue.getStatus()))
                .createdAt(issue.getCreatedAt())
                .closedAt(issue.getClosedAt())
                .author(GithubAccountEntity.of(issue.getAuthor()))
                .htmlUrl(issue.getHtmlUrl())
                .body(issue.getBody())
                .commentsCount(issue.getCommentsCount())
                .assignees(issue.getAssignees().stream().map(GithubAccountEntity::of).toList())
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
