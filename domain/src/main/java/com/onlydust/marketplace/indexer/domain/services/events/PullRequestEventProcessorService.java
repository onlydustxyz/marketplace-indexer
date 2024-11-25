package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawPullRequestEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.PullRequestIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Transactional
public class PullRequestEventProcessorService implements EventHandler<RawPullRequestEvent> {
    private final Exposer<CleanRepo> repoExposer;
    private final RawStorageWriter rawStorageWriter;
    private final PullRequestIndexer pullRequestIndexer;
    private final GithubAppContext githubAppContext;
    private final GithubObserver githubObserver;

    @Override
    public void process(RawPullRequestEvent event) {

        githubObserver.on(event);
        rawStorageWriter.savePullRequest(event.getPullRequest());

        githubAppContext.withGithubApp(event.getInstallation().getId(), () ->
                pullRequestIndexer.indexPullRequest(event.getRepository().getOwner().getLogin(),
                        event.getRepository().getName(),
                        event.getPullRequest().getNumber()
                ).ifPresent(pr -> repoExposer.expose(pr.getRepo()))
        );
    }
}
