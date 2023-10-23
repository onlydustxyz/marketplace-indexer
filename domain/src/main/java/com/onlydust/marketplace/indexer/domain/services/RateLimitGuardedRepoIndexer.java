package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.RateLimit;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.RateLimitService;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.time.Instant;

@Slf4j
public class RateLimitGuardedRepoIndexer implements RepoIndexer {
    final RepoIndexer indexer;
    final RateLimitService rateLimitService;
    final RateLimitService.Config config;
    RateLimit rateLimit;

    public RateLimitGuardedRepoIndexer(RepoIndexer indexer, RateLimitService rateLimitService, RateLimitService.Config config, MeterRegistry meterRegistry) {
        this.indexer = indexer;
        this.rateLimitService = rateLimitService;
        this.config = config;
        meterRegistry.gauge("indexer.rate_limit.remaining", this, RateLimitGuardedRepoIndexer::remainingRateLimit);
    }

    @Override
    public CleanRepo indexRepo(Long repoId) {
        if (remainingRateLimit() < config.getRepoThreshold()) {
            sleepUntilReset();
        }
        return indexer.indexRepo(repoId);
    }

    private Integer remainingRateLimit() {
        rateLimit = rateLimitService.rateLimit();
        return rateLimit.remaining();
    }

    private void sleepUntilReset() {
        try {
            LOGGER.info("Rate limit reached, waiting for reset");
            Thread.sleep(Duration.between(Instant.now(), rateLimit.resetAt().toInstant()).toMillis());
        } catch (InterruptedException e) {
            throw OnlyDustException.internalServerError("Timer interruption", e);
        }
    }
}
