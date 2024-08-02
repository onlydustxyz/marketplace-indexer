package com.onlydust.marketplace.indexer.domain.models.raw;


import lombok.*;

import java.util.List;
import java.util.Optional;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
@AllArgsConstructor
public class RawCommit extends JsonDocument {
    @NonNull
    String sha;
    RawShortAccount author;
    RawShortAccount committer;
    List<RawCommitFile> files;
    List<RawCommit> parents;

    public RawCommit sanitized() {
        return new RawCommit(
                sha,
                author,
                committer,
                Optional.ofNullable(files).map(files -> files.stream().map(RawCommitFile::sanitized).toList()).orElse(null),
                parents
        );
    }

    public boolean nonMerge() {
        return parents == null || parents.size() == 1;
    }
}
