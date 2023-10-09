package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.List;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "pull_request_commits", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PullRequestCommits {
    @Id
    Long pullRequestId;

    @Type(type = "jsonb")
    List<RawCommit> data;

    @EqualsAndHashCode.Exclude
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    Instant createdAt;

    @EqualsAndHashCode.Exclude
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    Instant updatedAt;

    public static PullRequestCommits of(Long pullRequestId, List<RawCommit> commits) {
        return PullRequestCommits.builder().pullRequestId(pullRequestId).data(commits).build();
    }
}
