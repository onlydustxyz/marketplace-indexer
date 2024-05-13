package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawIssue;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;


@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "issues", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.issues (data, number, repo_id, id) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawIssueEntity {
    @Id
    Long id;

    Long repoId;

    Long number;

    @JdbcTypeCode(SqlTypes.JSON)
    RawIssue data;

    public static RawIssueEntity of(Long repoId, RawIssue issue) {
        return RawIssueEntity.builder().id(issue.getId()).repoId(repoId).number(issue.getNumber()).data(issue).build();
    }
}
