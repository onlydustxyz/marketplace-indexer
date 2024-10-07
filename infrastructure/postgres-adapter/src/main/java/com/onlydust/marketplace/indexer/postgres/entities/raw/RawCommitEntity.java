package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
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
@Table(name = "commits", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.commits (author_id, author_name, data, repo_id, sha) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawCommitEntity {
    @Id
    String sha;

    Long repoId;

    Long authorId;

    String authorName;

    @JdbcTypeCode(SqlTypes.JSON)
    RawCommit data;

    public static RawCommitEntity of(Long repoId, RawCommit commit) {
        return RawCommitEntity.builder()
                .sha(commit.getSha())
                .repoId(repoId)
                .authorId(commit.getAuthor() == null ? null : commit.getAuthor().getId())
                .authorName(commit.getAuthor() == null ? null : commit.getAuthor().getLogin())
                .data(commit)
                .build();
    }
}
