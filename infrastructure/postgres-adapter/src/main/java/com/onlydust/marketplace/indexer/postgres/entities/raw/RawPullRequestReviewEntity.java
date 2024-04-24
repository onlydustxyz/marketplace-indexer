package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCodeReview;
import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import lombok.*;
import org.hibernate.annotations.SQLInsert;
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
@Table(name = "pull_request_reviews", schema = "indexer_raw")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
@SQLInsert(sql = "INSERT INTO indexer_raw.pull_request_reviews (data, pull_request_id) VALUES (?, ?) ON CONFLICT DO NOTHING")
public class RawPullRequestReviewEntity {
    @Id
    Long pullRequestId;

    @Type(type = "jsonb")
    List<RawCodeReview> data;

    public static RawPullRequestReviewEntity of(Long pullRequestId, List<RawCodeReview> codeReviews) {
        return RawPullRequestReviewEntity.builder().pullRequestId(pullRequestId).data(codeReviews).build();
    }
}
