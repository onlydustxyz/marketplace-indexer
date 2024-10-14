package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@Table(name = "github_repo_languages", schema = "indexer_exp")
@IdClass(GithubRepoLanguageEntity.PrimaryKey.class)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
