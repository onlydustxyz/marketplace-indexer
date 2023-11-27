package com.onlydust.marketplace.indexer.domain.services.monitoring;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class MonitoredFullRepoIndexer implements RepoIndexer {
    final RepoIndexer indexer;
    final MeterRegistry registry;

    @Override
    public Optional<CleanRepo> indexRepo(Long repoId) {
        final var repo = LongTaskTimer
                .builder("indexer.repo.duration")
                .tag("repoId", repoId.toString())
                .register(registry)
                .record(() -> indexer.indexRepo(repoId));

        if (repo != null && repo.isPresent())
            Counter
                    .builder("indexer.repo.count")
                    .tag("repoId", repoId.toString())
                    .register(registry)
                    .increment();

        return repo;
    }

    @Override
    public Optional<CleanRepo> indexRepo(String repoOwner, String repoName) {
        final var repo = LongTaskTimer
                .builder("indexer.repo.duration")
                .tag("repoOwner", repoOwner)
                .tag("repoName", repoName)
                .register(registry)
                .record(() -> indexer.indexRepo(repoOwner, repoName));

        if (repo != null && repo.isPresent())
            Counter
                    .builder("indexer.repo.count")
                    .tag("repoOwner", repoOwner)
                    .tag("repoName", repoName)
                    .register(registry)
                    .increment();

        return repo;
    }
}
