package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.github_app_events.StarEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawStarEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Transactional
public class StarEventProcessorService implements EventHandler<RawStarEvent> {
    private final RepoIndexer repoIndexer;

    @Override
    public void process(RawStarEvent rawEvent) {
        final var event = StarEvent.of(rawEvent);
        repoIndexer.indexRepo(event.getRepository().getId());
    }
}
