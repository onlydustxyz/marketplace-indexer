package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Entity
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@IdClass(GithubUserFileExtensionEntity.PrimaryKey.class)
@Table(name = "github_user_file_extension", schema = "indexer_exp")
public class GithubUserFileExtensionEntity {
    @Id
    Long userId;

    @Id
    String fileExtension;

    int commitCount;

    public void addCommit() {
        this.commitCount += 1;
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    public static class PrimaryKey implements Serializable {
        Long userId;
        String fileExtension;
    }
}
