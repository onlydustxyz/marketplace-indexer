package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestDiff;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "pull_requests_diff", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLInsert(sql = "INSERT INTO indexer_raw.pull_requests_diff (data, pull_request_id) VALUES (?, ?) ON CONFLICT DO NOTHING")
public class RawPullRequestDiffEntity {
    @Id
    Long pullRequestId;

    @Type(type = "jsonb")
    RawPullRequestDiff data;

    public static RawPullRequestDiffEntity of(Long pullRequestId, RawPullRequestDiff diff) {
        return RawPullRequestDiffEntity.builder().pullRequestId(pullRequestId).data(diff).build();
    }
}
