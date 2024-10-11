package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawLanguages;
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
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "repo_languages", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.repo_languages (data, repo_id) VALUES (?, ?) ON CONFLICT DO NOTHING")
public class RawRepoLanguagesEntity {
    @Id
    Long repoId;

    @JdbcTypeCode(SqlTypes.JSON)
    RawLanguages data;

    public static RawRepoLanguagesEntity of(Long repoId, RawLanguages languages) {
        return RawRepoLanguagesEntity.builder().repoId(repoId).data(languages).build();
    }
}
