package com.onlydust.marketplace.indexer.domain.services.observers;

import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import lombok.AllArgsConstructor;
import onlydust.com.marketplace.kernel.model.event.OnContributionChanged;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;

@AllArgsConstructor
public class IndexingOutboxObserver implements IndexingObserver {
    private final OutboxPort outboxPort;

    @Override
    public void onContributionsChanged(Long repoId) {
        outboxPort.push(OnContributionChanged.builder().repoId(repoId).build());
    }
}
