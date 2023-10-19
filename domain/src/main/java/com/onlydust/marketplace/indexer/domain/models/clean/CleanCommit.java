package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;

@Builder(access = AccessLevel.PRIVATE)
@Value
public class CleanCommit {
    String sha;
    CleanAccount author;

    public static CleanCommit of(RawCommit commit, CleanAccount author) {
        return CleanCommit.builder()
                .sha(commit.getSha())
                .author(author)
                .build();
    }
}
