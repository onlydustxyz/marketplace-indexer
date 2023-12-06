package com.onlydust.marketplace.indexer.postgres.entities.exposition;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@IdClass(GithubPullRequestCommitCountEntity.PrimaryKey.class)
@Table(name = "github_pull_request_commit_counts", schema = "indexer_exp")
public class GithubPullRequestCommitCountEntity {
    @Id
    Long pullRequestId;

    @Id
    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    GithubAccountEntity author;

    Integer commitCount;

    public static GithubPullRequestCommitCountEntity of(Long pullRequestId, GithubAccountEntity author, Long commitCount) {
        return GithubPullRequestCommitCountEntity.builder()
                .author(author)
                .pullRequestId(pullRequestId)
                .commitCount(Math.toIntExact(commitCount))
                .build();
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
