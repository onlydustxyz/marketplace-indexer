package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
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

    public static PullRequestCommits of(Long pullRequestId, List<RawCommit> commits) {
        return PullRequestCommits.builder().pullRequestId(pullRequestId).data(commits).build();
    }
}
