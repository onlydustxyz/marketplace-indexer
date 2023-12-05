package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

@AllArgsConstructor
@Slf4j
@Transactional
public class IssueEventProcessorService implements EventHandler<RawIssueEvent> {
    private final IssueIndexer issueIndexer;
    private final Exposer<CleanRepo> repoExposer;
    private final GithubAppContext githubAppContext;

    @Override
    public void process(RawIssueEvent event) {
        githubAppContext.withGithubApp(event.getInstallation().getId(), () ->
                issueIndexer.indexIssue(event.getRepository().getOwner().getLogin(), event.getRepository().getName(), event.getIssue().getNumber())
                        .ifPresent(issue -> repoExposer.expose(issue.getRepo()))
        );
    }
}
