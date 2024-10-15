package com.onlydust.marketplace.indexer.domain.models.clean;

import com.onlydust.marketplace.indexer.domain.models.raw.RawCommit;
import lombok.Builder;
import lombok.Value;

import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toMap;

@Builder(toBuilder = true)
@Value
public class CleanCommit {
    String sha;
    CleanAccount author;
    Map<String, Integer> modifiedFiles;

    public static CleanCommit of(RawCommit commit) {
        return CleanCommit.builder()
                .sha(commit.getSha())
                .author(commit.getAuthor() == null ? null : CleanAccount.of(commit.getAuthor()))
                .modifiedFiles(commit.getFiles().stream()
                        .map(f -> Map.entry(f.getFilename(), f.getChanges()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum)))
                .build();
    }

    public CleanCommit withAuthor(CleanAccount author) {
        return toBuilder().author(author).build();
    }

    public Optional<Long> authorId() {
        return Optional.ofNullable(author).flatMap(a -> Optional.ofNullable(a.getId()));
    }
}
