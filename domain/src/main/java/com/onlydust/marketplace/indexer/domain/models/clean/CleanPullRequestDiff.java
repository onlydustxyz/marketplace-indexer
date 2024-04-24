package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestDiff;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@Builder(access = AccessLevel.PRIVATE)
@Value
public class CleanPullRequestDiff {
    public static CleanPullRequestDiff of(RawPullRequestDiff diff) {
        return CleanPullRequestDiff.builder()
                .build();
    }
}
