package com.onlydust.marketplace.indexer.domain.services.monitoring;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;

public class MonitoredRepoIndexer implements RepoIndexer {
    final RepoIndexer indexer;
    final LongTaskTimer timer;
    final Counter counter;

    public MonitoredRepoIndexer(RepoIndexer indexer, MeterRegistry registry) {
        this.indexer = indexer;
        this.timer = LongTaskTimer.builder("indexer.repo.duration").register(registry);
        this.counter = Counter.builder("indexer.repo.count").register(registry);
    }

    @Override
    public CleanRepo indexRepo(Long repoId) {
        counter.increment();
        return timer.record(() -> indexer.indexRepo(repoId));
    }
}
