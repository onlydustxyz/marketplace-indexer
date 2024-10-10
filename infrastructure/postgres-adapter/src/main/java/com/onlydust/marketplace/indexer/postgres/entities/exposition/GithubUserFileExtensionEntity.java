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
    Long userId;

    @Id
    String fileExtension;

    int commitCount;
    int fileCount;
    int modificationCount;

    public GithubUserFileExtensionEntity(Long userId, String fileExtension) {
        this.userId = userId;
        this.fileExtension = fileExtension;
    }

    public void add(int commitCount, int fileCount, int modificationCount) {
        this.commitCount += commitCount;
        this.fileCount += fileCount;
        this.modificationCount += modificationCount;
    }

    @EqualsAndHashCode
    public static class PrimaryKey implements Serializable {
        Long userId;
        String fileExtension;
    }
}
