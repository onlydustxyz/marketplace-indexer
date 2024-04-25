package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestDiff;
import lombok.AccessLevel;
import lombok.Builder;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Builder(access = AccessLevel.PRIVATE)
public record CleanPullRequestDiff(Map<String, Long> modifiedFiles) {
    public static CleanPullRequestDiff of(RawPullRequestDiff diff) {
        return CleanPullRequestDiff.builder()
                .modifiedFiles(diff.getModifiedFiles().stream().collect(toMap(RawPullRequestDiff.FileStat::path, f -> f.linesAdded() + f.linesDeleted())))
                .build();
    }
}
