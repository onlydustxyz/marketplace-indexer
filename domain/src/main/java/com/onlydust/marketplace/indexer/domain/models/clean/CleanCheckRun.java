package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCheckRuns;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@Builder(access = AccessLevel.PRIVATE)
@Value
public class CleanCheckRun {
    Long id;

    public static CleanCheckRun of(RawCheckRuns.CheckRun checkRun) {
        return CleanCheckRun.builder()
                .id(checkRun.getId())
                .build();
    }
}
