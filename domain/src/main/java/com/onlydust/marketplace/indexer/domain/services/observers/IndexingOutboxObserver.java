package com.onlydust.marketplace.indexer.domain.services.observers;

import com.onlydust.marketplace.indexer.domain.models.exposition.Contribution;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import lombok.AllArgsConstructor;
import onlydust.com.marketplace.kernel.model.event.OnNewContribution;
import onlydust.com.marketplace.kernel.port.output.OutboxPort;

import java.util.Arrays;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@AllArgsConstructor
public class IndexingOutboxObserver implements IndexingObserver {
    private final OutboxPort outboxPort;

    @Override
    public void onNewContributions(Set<Long> repoIds) {
        if (!repoIds.isEmpty())
            outboxPort.push(OnNewContribution.builder().repoIds(repoIds).build());
    }

    @Override
    public void onNewContributions(Contribution... contributions) {
        onNewContributions(Arrays.stream(contributions).map(Contribution::getRepo).map(GithubRepo::getId).collect(toSet()));
    }
}
