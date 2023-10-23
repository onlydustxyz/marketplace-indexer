package com.onlydust.marketplace.indexer.domain.services.monitoring;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;

public class MonitoredUserIndexer implements UserIndexer {
    final UserIndexer indexer;
    final LongTaskTimer timer;
    final Counter counter;

    public MonitoredUserIndexer(UserIndexer indexer, MeterRegistry registry) {
        this.indexer = indexer;
        this.timer = LongTaskTimer.builder("indexer.user.duration").register(registry);
        this.counter = Counter.builder("indexer.user.count").register(registry);
    }

    @Override
    public CleanAccount indexUser(Long userId) {
        counter.increment();
        return timer.record(() -> indexer.indexUser(userId));
    }
}
