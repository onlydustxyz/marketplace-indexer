package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "pull_requests", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.pull_requests (data, number, repo_id, id) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawPullRequestEntity {
    @Id
    Long id;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    RawRepoEntity repo;

    Long number;

    @JdbcTypeCode(SqlTypes.JSON)
    RawPullRequest data;

    public static RawPullRequestEntity of(Long repoId, RawPullRequest pullRequest) {
        final var repo = RawRepoEntity.builder().id(repoId).build();
        return RawPullRequestEntity.builder().id(pullRequest.getId()).repo(repo).number(pullRequest.getNumber()).data(pullRequest).build();
    }
}
