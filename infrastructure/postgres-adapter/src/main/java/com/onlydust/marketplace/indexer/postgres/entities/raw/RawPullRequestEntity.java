package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toSet;


@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Builder(access = AccessLevel.PRIVATE, toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Table(name = "pull_requests", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.pull_requests (data, number, repo_id, id) VALUES (?, ?, ?, ?) ON CONFLICT DO NOTHING")
public class RawPullRequestEntity {
    @Id
    @NonNull
    final Long id;

    @NonNull
    final Long repoId;

    @NonNull
    final Long number;

    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "pull_request_commits",
            schema = "indexer_raw",
            joinColumns = @JoinColumn(name = "pull_request_id"),
            inverseJoinColumns = @JoinColumn(name = "commit_sha")
    )
    final Set<RawCommitEntity> commits;

    @Setter
    @JdbcTypeCode(SqlTypes.JSON)
    RawPullRequest data;

    public static RawPullRequestEntity of(RawPullRequest pullRequest) {
        return RawPullRequestEntity.builder()
                .id(pullRequest.getId())
                .repoId(pullRequest.getBase().getRepo().getId())
                .number(pullRequest.getNumber())
                .data(pullRequest)
                .build();
    }

    public RawPullRequestEntity withCommits(List<RawCommit> commits) {
        return toBuilder()
                .commits(commits.stream().map(c -> RawCommitEntity.of(repoId, c)).collect(toSet()))
                .build();
    }
}
