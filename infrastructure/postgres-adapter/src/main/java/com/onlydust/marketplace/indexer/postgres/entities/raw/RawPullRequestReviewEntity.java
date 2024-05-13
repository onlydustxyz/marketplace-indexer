package com.onlydust.marketplace.indexer.postgres.entities.raw;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCodeReview;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.type.SqlTypes;

import java.util.List;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Table(name = "pull_request_reviews", schema = "indexer_raw")
@SQLInsert(sql = "INSERT INTO indexer_raw.pull_request_reviews (data, pull_request_id) VALUES (?, ?) ON CONFLICT DO NOTHING")
public class RawPullRequestReviewEntity {
    @Id
    Long pullRequestId;

    @JdbcTypeCode(SqlTypes.JSON)
    List<RawCodeReview> data;

    public static RawPullRequestReviewEntity of(Long pullRequestId, List<RawCodeReview> codeReviews) {
        return RawPullRequestReviewEntity.builder().pullRequestId(pullRequestId).data(codeReviews).build();
    }
}
