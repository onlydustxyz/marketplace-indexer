package com.onlydust.marketplace.indexer.domain.models.raw;


import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
public class RawCommit extends JsonDocument {
    String sha;
    RawUser author;
    RawUser committer;
}
