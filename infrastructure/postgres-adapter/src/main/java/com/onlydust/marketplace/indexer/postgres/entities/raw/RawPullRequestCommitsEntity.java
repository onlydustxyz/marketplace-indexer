package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;

import java.util.List;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "pull_request_commits", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.pull_request_commits (data, pull_request_id) VALUES (?, ?) ON CONFLICT DO NOTHING")
public class RawPullRequestCommitsEntity {
    @Id
    Long pullRequestId;

    @JdbcTypeCode(SqlTypes.JSON)
    List<RawCommit> data;

    public static RawPullRequestCommitsEntity of(Long pullRequestId, List<RawCommit> commits) {
        return RawPullRequestCommitsEntity.builder().pullRequestId(pullRequestId).data(commits).build();
    }
}
