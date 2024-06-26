package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;

@Entity
@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Table(name = "github_repo_languages", schema = "indexer_exp")
@IdClass(GithubRepoLanguageEntity.PrimaryKey.class)
public class GithubRepoLanguageEntity {
    @Id
    Long repoId;
    @Id
    String language;
    Long lineCount;

    @EqualsAndHashCode
    public static class PrimaryKey implements Serializable {
        Long repoId;
        String language;
    }
}
