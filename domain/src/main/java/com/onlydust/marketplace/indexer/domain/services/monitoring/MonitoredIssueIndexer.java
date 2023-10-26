package com.onlydust.marketplace.indexer.domain.services.monitoring;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanIssue;
import com.onlydust.marketplace.indexer.domain.ports.in.IssueIndexer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.Optional;

public class MonitoredIssueIndexer implements IssueIndexer {
    final IssueIndexer indexer;
    final LongTaskTimer timer;
    final Counter counter;

    public MonitoredIssueIndexer(IssueIndexer indexer, MeterRegistry registry) {
        this.indexer = indexer;
        this.timer = LongTaskTimer.builder("indexer.issue.duration").register(registry);
        this.counter = Counter.builder("indexer.issue.count").register(registry);
    }

    @Override
    public Optional<CleanIssue> indexIssue(String repoOwner, String repoName, Long issueNumber) {
        counter.increment();
        return timer.record(() -> indexer.indexIssue(repoOwner, repoName, issueNumber));
    }
}
