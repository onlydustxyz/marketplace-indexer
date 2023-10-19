package com.onlydust.marketplace.indexer.domain.models;

import java.util.Set;

public record RepoIndexingJobData(Long installationId, Set<Long> repos) {
}
