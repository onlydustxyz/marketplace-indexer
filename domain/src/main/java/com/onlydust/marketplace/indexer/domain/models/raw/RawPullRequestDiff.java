package com.onlydust.marketplace.indexer.domain.models.raw;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
public class RawPullRequestDiff extends JsonDocument {
    List<FileStat> modifiedFiles;

    public record FileStat(String filename, Integer linesAdded, Integer linesDeleted) {
    }
}
