package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawIssueEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.Exposer;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.IssueIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.IndexingObserver;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.ContributionStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.IssueStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageWriter;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onlydust.com.marketplace.kernel.model.ContributionUUID;

@AllArgsConstructor
@Slf4j
@Transactional
public class IssueEventProcessorService implements EventHandler<RawIssueEvent> {
    private final IssueIndexer issueIndexer;
    private final UserIndexer userIndexer;
    private final Exposer<CleanRepo> repoExposer;
    private final GithubAppContext githubAppContext;
    private final RawStorageWriter rawStorageWriter;
    private final IssueStorage issueStorage;
    private final ContributionStorage contributionStorage;
    private final GithubObserver githubObserver;
    private final IndexingObserver indexingObserver;

    @Override
    public void process(RawIssueEvent event) {
        if (event.getIssue().getPullRequest() != null)
            return;

        githubObserver.on(event);

        switch (event.getAction()) {
            case "transferred", "deleted" -> onIssueTransferredOrDeleted(event);
            case "assigned" -> onIssueAssigned(event);
            case "unassigned" -> onIssueUnassigned(event);
            default -> onIssueUpdated(event);
        }
    }

    private void onIssueTransferredOrDeleted(RawIssueEvent event) {
        rawStorageWriter.deleteIssue(event.getIssue().getId());
        contributionStorage.deleteAllByRepoIdAndGithubNumber(event.getRepository().getId(), event.getIssue().getNumber());
        issueStorage.delete(event.getIssue().getId());
        indexingObserver.onContributionsChanged(event.getRepository().getId(), ContributionUUID.of(event.getIssue().getId()));
    }

    private void onIssueAssigned(RawIssueEvent event) {
        githubAppContext.withGithubApp(event.getInstallation().getId(), () ->
                issueIndexer.indexIssue(event.getRepository().getOwner().getLogin(), event.getRepository().getName(), event.getIssue().getNumber())
                        .ifPresent(issue -> {
                            repoExposer.expose(issue.getRepo());
                            userIndexer.indexUser(event.getAssignee().getId()).ifPresent(assignee ->
                                    userIndexer.indexUser(event.getSender().getId()).ifPresent(assignedBy -> {
                                        issueStorage.saveAssignee(event.getIssue().getId(), GithubAccount.of(assignee), GithubAccount.of(assignedBy));
                                        indexingObserver.onContributionsChanged(event.getRepository().getId(), ContributionUUID.of(event.getIssue().getId()));
                                    }));
                        })
        );
    }

    private void onIssueUnassigned(RawIssueEvent event) {
        issueStorage.deleteAssignee(event.getIssue().getId(), event.getAssignee().getId());
        indexingObserver.onContributionsChanged(event.getRepository().getId(), ContributionUUID.of(event.getIssue().getId()));
    }

    private void onIssueUpdated(RawIssueEvent event) {
        githubAppContext.withGithubApp(event.getInstallation().getId(), () ->
                issueIndexer.indexIssue(event.getRepository().getOwner().getLogin(), event.getRepository().getName(), event.getIssue().getNumber())
                        .ifPresent(issue -> repoExposer.expose(issue.getRepo()))
        );
    }
}
