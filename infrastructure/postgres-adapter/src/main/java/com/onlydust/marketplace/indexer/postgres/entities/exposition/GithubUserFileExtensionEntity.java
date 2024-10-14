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
@Table(name = "github_user_file_extensions", schema = "indexer_exp")
public class GithubUserFileExtensionEntity {
    @Id
    final Long userId;

    @Id
    final String fileExtension;

    int commitCount;
    int fileCount;
    int modificationCount;

    public void add(int commitCount, int fileCount, int modificationCount) {
        this.commitCount += commitCount;
        this.fileCount += fileCount;
        this.modificationCount += modificationCount;
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PrimaryKey implements Serializable {
        Long userId;
        String fileExtension;
    }
}
