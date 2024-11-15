package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Table(name = "github_issues_assignees", schema = "indexer_exp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@IdClass(GithubIssueAssigneeEntity.PrimaryKey.class)
public class GithubIssueAssigneeEntity {
    @Id
    Long issueId;

    @Id
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubAccountEntity user;
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubAccountEntity assignedByUser;

    public static GithubIssueAssigneeEntity of(Long issueId, GithubAccount assignee) {
        return GithubIssueAssigneeEntity.builder()
                .issueId(issueId)
                .user(GithubAccountEntity.of(assignee))
                .build();
    }

    public static GithubIssueAssigneeEntity of(Long issueId, GithubAccount assignee, GithubAccount assignedBy) {
        return GithubIssueAssigneeEntity.builder()
                .issueId(issueId)
                .user(GithubAccountEntity.of(assignee))
                .assignedByUser(GithubAccountEntity.of(assignedBy))
                .build();
    }

    @EqualsAndHashCode
    public static class PrimaryKey implements Serializable {
        Long issueId;
        GithubAccountEntity user;
    }
}
