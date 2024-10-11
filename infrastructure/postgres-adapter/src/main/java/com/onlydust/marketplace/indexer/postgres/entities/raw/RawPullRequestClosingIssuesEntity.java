package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestClosingIssues;
import jakarta.persistence.Entity;
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
@NoArgsConstructor
@IdClass(RawPullRequestClosingIssuesEntity.Id.class)
@Table(name = "pull_request_closing_issues", schema = "indexer_raw")
@SQLInsert(sql = """
                INSERT INTO indexer_raw.pull_request_closing_issues (data, pull_request_number, repo_name, repo_owner)
                VALUES (?, ?, ?, ?)
                ON CONFLICT DO NOTHING
        """)
public class RawPullRequestClosingIssuesEntity {
    @jakarta.persistence.Id
    String repoOwner;

    @jakarta.persistence.Id
    String repoName;

    @jakarta.persistence.Id
    Long pullRequestNumber;

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
    public static class Id implements Serializable {
        String repoOwner;
        String repoName;
        Long pullRequestNumber;
    }
}

