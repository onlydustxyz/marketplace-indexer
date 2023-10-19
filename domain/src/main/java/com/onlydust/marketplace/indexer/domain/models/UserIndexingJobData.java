package com.onlydust.marketplace.indexer.domain.models;

import java.util.Set;

public record UserIndexingJobData(Set<Long> users) {
}
