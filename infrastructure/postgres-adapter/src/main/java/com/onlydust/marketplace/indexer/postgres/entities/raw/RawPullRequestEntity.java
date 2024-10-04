package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pull_requests", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.pull_requests (data, number, repo_id, id) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawPullRequestEntity {
    @Id
    Long id;

    Long repoId;

    Long number;

    @JdbcTypeCode(SqlTypes.JSON)
    RawPullRequest data;

    public static RawPullRequestEntity of(RawPullRequest pullRequest) {
        return RawPullRequestEntity.builder()
                .id(pullRequest.getId())
                .repoId(pullRequest.getBase().getRepo().getId())
                .number(pullRequest.getNumber())
                .data(pullRequest)
                .build();
    }
}
