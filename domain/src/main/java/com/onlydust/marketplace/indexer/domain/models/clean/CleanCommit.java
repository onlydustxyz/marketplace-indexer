package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import lombok.Builder;
import lombok.Value;

import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Builder
@Value
public class CleanCommit {
    String sha;
    CleanAccount author;
    Map<String, Integer> modifiedFiles;

    public static CleanCommit of(RawCommit commit, CleanAccount author) {
        return CleanCommit.builder()
                .sha(commit.getSha())
                .author(author)
                .modifiedFiles(commit.getFiles().stream()
                        .map(f -> Map.entry(f.getFilename(), f.getChanges()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue)))
                .build();
    }
}
