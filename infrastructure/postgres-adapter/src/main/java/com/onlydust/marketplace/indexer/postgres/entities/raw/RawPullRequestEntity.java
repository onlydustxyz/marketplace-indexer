package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "pull_requests", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.pull_requests (data, number, repo_id, id) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawPullRequestEntity {
    @Id
    Long id;

    Long repoId;

    Long number;

    @JdbcTypeCode(SqlTypes.JSON)
    RawPullRequest data;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "pull_request_commits",
            schema = "indexer_raw",
            joinColumns = @JoinColumn(name = "pull_request_id"),
            inverseJoinColumns = @JoinColumn(name = "commit_sha")
    )
    Set<RawCommitEntity> commits;

    public static RawPullRequestEntity of(RawPullRequest pullRequest) {
        return RawPullRequestEntity.builder()
                .id(pullRequest.getId())
                .repoId(pullRequest.getBase().getRepo().getId())
                .number(pullRequest.getNumber())
                .data(pullRequest)
                .build();
    }

    public RawPullRequestEntity withCommits(List<RawCommit> commits) {
        if (this.commits == null) this.commits = new HashSet<>();
        this.commits.clear();
        this.commits.addAll(commits.stream().map(commit -> RawCommitEntity.of(repoId, commit)).toList());
        return this;
    }

    public RawPullRequestEntity withData(RawPullRequest pullRequest) {
        this.data = pullRequest;
        return this;
    }
}
