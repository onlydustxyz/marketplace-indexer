package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.models.clean.github_app_events.*;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubAppInstallationStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
@Transactional
public class InstallationEventProcessorService implements EventHandler<RawInstallationEvent> {
    private final RepoIndexingJobStorage repoIndexingJobStorage;
    private final GithubAppInstallationStorage githubAppInstallationStorage;

    @Override
    public void process(RawInstallationEvent rawEvent) {
        final var event = InstallationEvent.of(rawEvent);

        if (event instanceof InstallationAddedEvent addedEvent)
            onAdded(addedEvent);
        else if (event instanceof InstallationCreatedEvent createdEvent)
            onCreated(createdEvent);
        else if (event instanceof InstallationDeletedEvent deletedEvent)
            onDeleted(deletedEvent);
        else if (event instanceof InstallationRemovedEvent removedEvent)
            onRemoved(removedEvent);
        else if (event instanceof InstallationSuspendedEvent suspendedEvent)
            onSuspended(suspendedEvent);
        else if (event instanceof InstallationUnsuspendedEvent unsuspendedEvent)
            onUnsuspended(unsuspendedEvent);
        else if (event instanceof InstallationNewPermissionsAcceptedEvent newPermissionsAcceptedEvent)
            onNewPermissionsAccepted(newPermissionsAcceptedEvent);
    }

    private void onNewPermissionsAccepted(InstallationNewPermissionsAcceptedEvent event) {
        githubAppInstallationStorage.setPermissions(event.getInstallationId(), GithubAppInstallation.getPermissions(event));
    }

    private void onUnsuspended(InstallationUnsuspendedEvent event) {
        repoIndexingJobStorage.setSuspendedAt(event.getInstallationId(), null);
        githubAppInstallationStorage.setSuspendedAt(event.getInstallationId(), null);
    }

    private void onSuspended(InstallationSuspendedEvent event) {
        repoIndexingJobStorage.setSuspendedAt(event.getInstallationId(), event.getSuspendedAt());
        githubAppInstallationStorage.setSuspendedAt(event.getInstallationId(), event.getSuspendedAt());
    }

    private void onDeleted(InstallationDeletedEvent event) {
        onDeleted(event.getInstallationId());
    }

    private void onDeleted(Long installationId) {
        repoIndexingJobStorage.deleteInstallation(installationId);
        githubAppInstallationStorage.delete(installationId);
    }

    private void onCreated(InstallationCreatedEvent event) {
        final var owner = GithubAccount.of(event.getAccount());
        final var repos = event.getRepos().stream()
                .map(repo -> GithubRepo.of(repo, owner))
                .toList();

        githubAppInstallationStorage.findInstallationIdByAccount(event.getAccount().getId())
                .ifPresent(this::onDeleted);

        repoIndexingJobStorage.setInstallationForRepos(event.getInstallationId(), event.getRepos().stream()
                .map(repo -> new RepoIndexingJobTrigger(repo.getId(), false, repo.getIsPublic()))
                .toArray(RepoIndexingJobTrigger[]::new));

        githubAppInstallationStorage.save(GithubAppInstallation.of(event, owner, repos));
    }

    private void onAdded(InstallationAddedEvent event) {
        repoIndexingJobStorage.setInstallationForRepos(event.getInstallationId(), event.getReposAdded().stream()
                .map(repo -> new RepoIndexingJobTrigger(repo.getId(), false, repo.getIsPublic()))
                .toArray(RepoIndexingJobTrigger[]::new));

        githubAppInstallationStorage.addRepos(event.getInstallationId(),
                event.getReposAdded().stream()
                        .map(GithubRepo::of)
                        .toList());
    }

    private void onRemoved(InstallationRemovedEvent event) {
        repoIndexingJobStorage.deleteInstallationForRepos(event.getInstallationId(), event.getRepoIds());
        githubAppInstallationStorage.removeRepos(event.getInstallationId(), event.getRepoIds());
    }
}
