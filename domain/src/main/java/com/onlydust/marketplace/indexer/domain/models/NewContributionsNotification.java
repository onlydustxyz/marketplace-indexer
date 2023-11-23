package com.onlydust.marketplace.indexer.domain.models;

import java.time.Instant;
import java.util.Set;

public record NewContributionsNotification(Set<Long> repoIds, Instant latestContributionUpdate) {
}
