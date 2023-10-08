package com.onlydust.marketplace.indexer.postgres.entities;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCodeReview;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.UpdateTimestamp;

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
@Table(name = "pull_request_reviews", schema = "indexer_raw")
public class PullRequestReview {
    @Id
    Long pullRequestId;
    @Type(type = "jsonb")
    List<RawCodeReview> data;
    @CreationTimestamp
    ZonedDateTime createdAt;
    @UpdateTimestamp
    ZonedDateTime updatedAt;

    public static PullRequestReview of(Long pullRequestId, List<RawCodeReview> codeReviews) {
        return PullRequestReview.builder().pullRequestId(pullRequestId).data(codeReviews).build();
    }
}
