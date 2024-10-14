package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import com.onlydust.marketplace.indexer.domain.models.exposition.GithubCommit;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@AllArgsConstructor(staticName = "of")
@NoArgsConstructor(force = true)
@Getter
@Table(name = "github_commits", schema = "indexer_exp")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GithubCommitEntity {
    @Id
    String sha;

    Long pullRequestId;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    GithubAccountEntity author;

    public static GithubCommitEntity of(GithubCommit commit) {
        return GithubCommitEntity.builder()
                .sha(commit.getSha())
                .author(commit.getAuthorId().map(id -> GithubAccountEntity.of(commit.getAuthor())).orElse(null))
                .build();
    }
}
