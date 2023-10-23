package com.onlydust.marketplace.indexer.domain.ports.out;

import com.onlydust.marketplace.indexer.domain.models.RateLimit;
import lombok.Data;

public interface RateLimitService {
    RateLimit rateLimit();

    @Data
    class Config {
        Integer repoThreshold;
    }
}
