package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestClosingIssues;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.IdClass;
import javax.persistence.Table;
import java.io.Serializable;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@IdClass(PullRequestClosingIssues.Id.class)
@Table(name = "pull_request_closing_issues", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLInsert(sql = """
                INSERT INTO indexer_raw.pull_request_closing_issues (data, pull_request_number, repo_name, repo_owner)
                VALUES (?, ?, ?, ?)
                ON CONFLICT DO NOTHING
        """)
public class PullRequestClosingIssues {
    @javax.persistence.Id
    String repoOwner;

    @javax.persistence.Id
    String repoName;

    @javax.persistence.Id
    Long pullRequestNumber;

    @Type(type = "jsonb")
    RawPullRequestClosingIssues data;

    public static PullRequestClosingIssues of(String repoOwner, String repoName, Long pullRequestNumber, RawPullRequestClosingIssues closingIssues) {
        return PullRequestClosingIssues.builder()
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

