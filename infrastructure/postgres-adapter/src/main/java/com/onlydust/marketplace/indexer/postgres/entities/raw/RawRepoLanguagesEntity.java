package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawLanguages;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "repo_languages", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLInsert(sql = "INSERT INTO indexer_raw.repo_languages (data, repo_id) VALUES (?, ?) ON CONFLICT DO NOTHING")
public class RawRepoLanguagesEntity {
    @Id
    Long repoId;

    @Type(type = "jsonb")
    RawLanguages data;

    public static RawRepoLanguagesEntity of(Long repoId, RawLanguages languages) {
        return RawRepoLanguagesEntity.builder().repoId(repoId).data(languages).build();
    }
}
