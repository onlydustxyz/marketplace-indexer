package com.onlydust.marketplace.indexer.domain.models.raw;


import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@ToString(callSuper = true)
@Value
@AllArgsConstructor
public class RawCommitFile extends JsonDocument {
    String sha;
    String filename;
    String status;
    Integer additions;
    Integer deletions;
    Integer changes;
    String patch;

    public RawCommitFile sanitized() {
        return new RawCommitFile(
                sha,
                filename,
                status,
                additions,
                deletions,
                changes,
                ""
        );
    }
}
