package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestClosingIssues;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;


@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@IdClass(RawPullRequestClosingIssuesEntity.PrimaryKey.class)
@Table(name = "pull_request_closing_issues", schema = "indexer_raw")
@SQLInsert(sql = """
                INSERT INTO indexer_raw.pull_request_closing_issues (data, pull_request_number, repo_name, repo_owner)
                VALUES (?, ?, ?, ?)
                ON CONFLICT DO NOTHING
        """)
public class RawPullRequestClosingIssuesEntity {
    @Id
    final String repoOwner;

    @Id
    final String repoName;

    @Id
    final Long pullRequestNumber;

    @JdbcTypeCode(SqlTypes.JSON)
    RawPullRequestClosingIssues data;

    public static RawPullRequestClosingIssuesEntity of(String repoOwner, String repoName, Long pullRequestNumber, RawPullRequestClosingIssues closingIssues) {
        return RawPullRequestClosingIssuesEntity.builder()
                .repoOwner(repoOwner)
                .repoName(repoName)
                .pullRequestNumber(pullRequestNumber)
                .data(closingIssues)
                .build();
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrimaryKey implements Serializable {
        String repoOwner;
        String repoName;
        Long pullRequestNumber;
    }
}

