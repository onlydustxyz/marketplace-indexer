package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCodeReview;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@Builder(access = AccessLevel.PRIVATE)
@Value
public class CleanCodeReview {
    Long id;
    CleanAccount author;

    public static CleanCodeReview of(RawCodeReview review, CleanAccount author) {
        return CleanCodeReview.builder()
                .id(review.getId())
                .author(author)
                .build();
    }
}
