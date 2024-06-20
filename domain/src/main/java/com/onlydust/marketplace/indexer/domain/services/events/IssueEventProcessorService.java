package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Transactional
public class IssueEventProcessorService implements EventHandler<RawIssueEvent> {
    private final IssueIndexer issueIndexer;
    private final Exposer<CleanRepo> repoExposer;
    private final GithubAppContext githubAppContext;
    private final RawStorageWriter rawStorageWriter;
    private final IssueStorage issueStorage;
    private final ContributionStorage contributionStorage;
    private final GithubObserver githubObserver;

    @Override
    public void process(RawIssueEvent event) {
        final var action = event.getAction();

        githubObserver.on(event);

        if (action.equals("transferred") || action.equals("deleted")) {
            rawStorageWriter.deleteIssue(event.getIssue().getId());
            contributionStorage.deleteAllByRepoIdAndGithubNumber(event.getRepository().getId(), event.getIssue().getNumber());
            issueStorage.delete(event.getIssue().getId());
        } else {
            githubAppContext.withGithubApp(event.getInstallation().getId(), () ->
                    issueIndexer.indexIssue(event.getRepository().getOwner().getLogin(), event.getRepository().getName(), event.getIssue().getNumber())
                            .ifPresent(issue -> repoExposer.expose(issue.getRepo()))
            );
        }
    }
}
