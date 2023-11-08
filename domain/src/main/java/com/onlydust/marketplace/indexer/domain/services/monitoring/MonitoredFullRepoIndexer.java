package com.onlydust.marketplace.indexer.domain.services.monitoring;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Optional;

public class MonitoredFullRepoIndexer implements FullRepoIndexer {
    final FullRepoIndexer indexer;
    final LongTaskTimer timer;
    final Counter counter;

    public MonitoredFullRepoIndexer(FullRepoIndexer indexer, MeterRegistry registry) {
        this.indexer = indexer;
        this.timer = LongTaskTimer.builder("indexer.repo.duration").register(registry);
        this.counter = Counter.builder("indexer.repo.count").register(registry);
    }

    @Override
    public Optional<CleanRepo> indexFullRepo(Long repoId) {
        counter.increment();
        return timer.record(() -> indexer.indexFullRepo(repoId));
    }
}
