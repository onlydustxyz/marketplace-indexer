package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pull_request_commits", schema = "indexer_raw")
public class PullRequestCommits {
    @Id
    @Column(name = "pull_request_id")
    Long pullRequestId;
    @Column(name = "data")
    @Type(type = "jsonb")
    List<RawCommit> data;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    ZonedDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static PullRequestCommits of(Long pullRequestId, List<RawCommit> commits) {
        return PullRequestCommits.builder().pullRequestId(pullRequestId).data(commits).build();
    }
}
