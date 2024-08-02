package com.onlydust.marketplace.indexer.github.adapters;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.RateLimit;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import com.onlydust.marketplace.indexer.github.GithubHttpClient;
import lombok.AllArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
public class GithubRateLimitServiceAdapter implements RateLimitService {
    private final GithubHttpClient client;

    @Override
    public RateLimit rateLimit() {
        final var response = client.get("/rate_limit", RateLimitResponse.class)
                .orElseThrow(() -> OnlyDustException.internalServerError("Unable to fetch rate limit"));
        return new RateLimit(response.rate().remaining(), Instant.ofEpochSecond(response.rate().reset()));
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record RateLimitResponse(Rate rate) {
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record Rate(Integer remaining, Integer reset) {
        }
    }
}

