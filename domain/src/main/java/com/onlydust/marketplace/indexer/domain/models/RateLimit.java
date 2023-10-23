package com.onlydust.marketplace.indexer.domain.models;

import java.util.Date;

public record RateLimit(Integer remaining, Date resetAt) {
}
