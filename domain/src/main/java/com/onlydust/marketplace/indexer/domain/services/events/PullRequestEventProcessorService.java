package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawPullRequestEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

@AllArgsConstructor
@Slf4j
@Transactional
public class PullRequestEventProcessorService implements EventHandler<RawPullRequestEvent> {
    private final Exposer<CleanRepo> repoExposer;
    private final PullRequestIndexer pullRequestIndexer;

    @Override
    public void process(RawPullRequestEvent rawEvent) {
        pullRequestIndexer.indexPullRequest(rawEvent.getRepository().getOwner().getLogin(),
                rawEvent.getRepository().getName(),
                rawEvent.getPullRequest().getNumber()
        ).ifPresent(pr -> repoExposer.expose(pr.getRepo()));
    }
}
