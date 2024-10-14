package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import com.onlydust.marketplace.indexer.domain.models.raw.RawShortCommit;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;

@Getter
@Entity
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "commits", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.commits (author_id, author_name, data, repo_id, sha) VALUES (?, ?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawCommitEntity {
    @Id
    final String sha;

    final Long repoId;

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

    public static RawCommitEntity of(Long repoId, RawShortCommit commit) {
        return RawCommitEntity.builder()
                .sha(commit.getSha())
                .repoId(repoId)
                .authorName(commit.getAuthor() == null ? null : commit.getAuthor().getName())
                .build();
    }
}
