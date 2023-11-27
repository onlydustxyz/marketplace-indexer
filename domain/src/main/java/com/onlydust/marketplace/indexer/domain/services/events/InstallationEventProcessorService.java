package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.models.clean.*;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepositoryEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.InstallationEventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubAppInstallationStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.RepoStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawInstallationEventStorage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;

@AllArgsConstructor
@Slf4j
@Transactional
public class InstallationEventProcessorService implements InstallationEventHandler {
    private final RawInstallationEventStorage rawInstallationEventStorage;
    private final RepoIndexingJobStorage repoIndexingJobStorage;
    private final GithubAppInstallationStorage githubAppInstallationStorage;
    private final RepoStorage githubRepoStorage;

    @Override
    public void process(RawInstallationEvent rawEvent) {
        rawInstallationEventStorage.save(rawEvent);

        final var event = mapRawEvent(rawEvent);
        if (event.getAction() == null) return;

        switch (event.getAction()) {
            case ADDED -> onAdded((InstallationAddedEvent) event);
            case CREATED -> onCreated((InstallationCreatedEvent) event);
            case DELETED -> onDeleted((InstallationDeletedEvent) event);
            case REMOVED -> onRemoved((InstallationRemovedEvent) event);
            case SUSPEND -> onSuspended((InstallationSuspendedEvent) event);
            case UNSUSPEND -> onUnsuspended((InstallationUnsuspendedEvent) event);
        }
    }

    @Override
    public void process(RawRepositoryEvent rawEvent) {
        final var event = RepositoryEvent.of(rawEvent);
        if (event.getAction() == null) return;
        switch (event.getAction()) {
            case PRIVATIZED -> onPrivatized(event);
            case PUBLICIZED -> onPublicized(event);
        }
    }

    private void onPublicized(RepositoryEvent event) {
        repoIndexingJobStorage.setPublic(event.getRepository().getId());
        githubRepoStorage.setPublic(event.getRepository().getId());
    }

    private void onPrivatized(RepositoryEvent event) {
        repoIndexingJobStorage.setPrivate(event.getRepository().getId());
        githubRepoStorage.setPrivate(event.getRepository().getId());
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

    private InstallationEvent mapRawEvent(RawInstallationEvent rawEvent) {
        final var action = InstallationEvent.Action.of(rawEvent.getAction());
        final var account = CleanAccount.of(rawEvent.getInstallation().getAccount());

        return switch (action) {
            case ADDED -> InstallationAddedEvent.of(
                    rawEvent,
                    rawEvent.getRepositoriesAdded().stream()
                            .map(repo -> CleanRepo.of(repo, account))
                            .toList());

            case CREATED -> InstallationCreatedEvent.of(
                    rawEvent,
                    CleanAccount.of(rawEvent.getInstallation().getAccount()),
                    rawEvent.getRepositories().stream()
                            .map(repo -> CleanRepo.of(repo, account))
                            .toList());

            case REMOVED -> InstallationRemovedEvent.of(rawEvent);

            case DELETED -> InstallationDeletedEvent.of(rawEvent);

            case SUSPEND -> InstallationSuspendedEvent.of(rawEvent);

            case UNSUSPEND -> InstallationUnsuspendedEvent.of(rawEvent);
        };
    }
}
