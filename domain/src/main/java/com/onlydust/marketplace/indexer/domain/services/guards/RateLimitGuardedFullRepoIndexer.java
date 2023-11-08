package com.onlydust.marketplace.indexer.domain.services.guards;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.RateLimit;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
public class RateLimitGuardedFullRepoIndexer implements FullRepoIndexer {
    final FullRepoIndexer indexer;
    final RateLimitService rateLimitService;
    final RateLimitService.Config config;
    final MeterRegistry meterRegistry;
    final GithubAppContext githubAppContext;

    private static void sleepUntil(Instant resetAt) {
        try {
            Thread.sleep(Duration.between(Instant.now(), resetAt).toMillis());
        } catch (InterruptedException e) {
            throw OnlyDustException.internalServerError("Timer interruption", e);
        }
    }

    @Override
    public Optional<CleanRepo> indexFullRepo(Long repoId) {
        final var rateLimit = rateLimit();

        if (rateLimit.remaining() < config.getFullRepoThreshold()) {
            LOGGER.info("Rate limit reached, waiting for reset until {}", rateLimit.resetAt());
            sleepUntil(rateLimit.resetAt());
        }

        return indexer.indexFullRepo(repoId);
    }

    private RateLimit rateLimit() {
        final var rateLimit = rateLimitService.rateLimit();
        Gauge
                .builder("indexer.rate_limit.remaining", rateLimit, RateLimit::remaining)
                .tag("installationId", githubAppContext.installationId().map(String::valueOf).orElse("null"))
                .register(meterRegistry);

        return rateLimit;
    }
}
