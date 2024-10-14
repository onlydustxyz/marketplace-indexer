package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Getter
@IdClass(GithubPullRequestCommitCountEntity.PrimaryKey.class)
@Table(name = "github_pull_request_commit_counts", schema = "indexer_exp")
public class GithubPullRequestCommitCountEntity {
    @Id
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    final GithubAccountEntity author;
    @Id
    Long pullRequestId;
    Integer commitCount;

    public static GithubPullRequestCommitCountEntity of(Long pullRequestId, GithubAccountEntity author, Long commitCount) {
        return GithubPullRequestCommitCountEntity.builder().author(author).pullRequestId(pullRequestId).commitCount(Math.toIntExact(commitCount)).build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @EqualsAndHashCode
    public static class PrimaryKey implements Serializable {
        Long pullRequestId;
        GithubAccountEntity author;
    }

}
