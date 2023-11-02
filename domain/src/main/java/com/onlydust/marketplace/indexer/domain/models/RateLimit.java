package com.onlydust.marketplace.indexer.domain.models;

import java.time.Instant;

public record RateLimit(Integer remaining, Instant resetAt) {
}
