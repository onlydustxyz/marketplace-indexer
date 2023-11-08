package com.onlydust.marketplace.indexer.domain.services.monitoring;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class MonitoredFullRepoIndexer implements FullRepoIndexer {
    final FullRepoIndexer indexer;
    final MeterRegistry registry;

    @Override
    public Optional<CleanRepo> indexFullRepo(Long repoId) {
        final var repo = LongTaskTimer
                .builder("indexer.repo.duration")
                .tag("repoId", repoId.toString())
                .register(registry)
                .record(() -> indexer.indexFullRepo(repoId));

        if (repo != null && repo.isPresent())
            Counter
                    .builder("indexer.repo.count")
                    .tag("repoId", repoId.toString())
                    .register(registry)
                    .increment();

        return repo;
    }
}
