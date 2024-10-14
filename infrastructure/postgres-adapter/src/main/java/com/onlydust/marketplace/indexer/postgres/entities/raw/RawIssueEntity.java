package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;


@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Table(name = "issues", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.issues (data, number, repo_id, id) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawIssueEntity {
    @Id
    final Long id;

    final Long repoId;

    final Long number;

    @JdbcTypeCode(SqlTypes.JSON)
    RawIssue data;

    public static RawIssueEntity of(Long repoId, RawIssue issue) {
        return RawIssueEntity.builder()
                .id(issue.getId())
                .repoId(repoId)
                .number(issue.getNumber())
                .data(issue)
                .build();
    }
}
