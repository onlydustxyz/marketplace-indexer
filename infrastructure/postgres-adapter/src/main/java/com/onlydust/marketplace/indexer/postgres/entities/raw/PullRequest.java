package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequest;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "pull_requests", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class PullRequest {
    @Id
    Long id;

    @EqualsAndHashCode.Exclude
    @ManyToOne
    Repo repo;

    Long number;

    @Type(type = "jsonb")
    RawPullRequest data;

    public static PullRequest of(Long repoId, RawPullRequest pullRequest) {
        final var repo = Repo.builder().id(repoId).build();
        return PullRequest.builder().id(pullRequest.getId()).repo(repo).number(pullRequest.getNumber()).data(pullRequest).build();
    }
}
