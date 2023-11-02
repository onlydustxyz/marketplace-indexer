package com.onlydust.marketplace.indexer.domain.services.monitoring;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanPullRequest;
import com.onlydust.marketplace.indexer.domain.ports.in.PullRequestIndexer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Optional;

public class MonitoredPullRequestIndexer implements PullRequestIndexer {
    final PullRequestIndexer indexer;
    final LongTaskTimer timer;
    final Counter counter;

    public MonitoredPullRequestIndexer(PullRequestIndexer indexer, MeterRegistry registry) {
        this.indexer = indexer;
        this.timer = LongTaskTimer.builder("indexer.pull_request.duration").register(registry);
        this.counter = Counter.builder("indexer.pull_request.count").register(registry);
    }

    @Override
    public Optional<CleanPullRequest> indexPullRequest(String repoOwner, String repoName, Long pullRequestNumber) {
        counter.increment();
        return timer.record(() -> indexer.indexPullRequest(repoOwner, repoName, pullRequestNumber));
    }
}
