package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.StarEvent;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawStarEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

@AllArgsConstructor
@Slf4j
@Transactional
public class StarEventProcessorService implements EventHandler<RawStarEvent> {
    private final RepoStorage githubRepoStorage;


    @Override
    public void process(RawStarEvent rawEvent) {
        final var event = StarEvent.of(rawEvent);
        githubRepoStorage.save(GithubRepo.of(event.getRepository()));
    }
}
