package com.onlydust.marketplace.indexer.domain.models;

import lombok.NonNull;

public record CommitIndexingJobItem(@NonNull Long repoId, @NonNull String sha) {
}
