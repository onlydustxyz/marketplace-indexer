package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.ZonedDateTime;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "pull_requests", schema = "indexer_raw")
public class PullRequest {
    @Id
    Long id;
    @OneToOne
    Repo repo;
    Long number;
    @Type(type = "jsonb")
    RawPullRequest data;
    @Column(name = "created_at", updatable = false)
    @CreationTimestamp
    ZonedDateTime createdAt;
    @Column(name = "updated_at")
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static PullRequest of(Long repoId, RawPullRequest pullRequest) {
        final var repo = Repo.builder().id(repoId).build();
        return PullRequest.builder().id(pullRequest.getId()).repo(repo).number(pullRequest.getNumber()).data(pullRequest).build();
    }
}
