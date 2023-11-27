package com.onlydust.marketplace.indexer.domain.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RepoIndexingJobTrigger {
    Long repoId;
    Boolean fullIndexing;
}
