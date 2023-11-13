package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.*;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.events.InstallationEventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubAppInstallationStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawInstallationEventStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Transactional
public class InstallationEventProcessorService implements InstallationEventHandler {
    private final RawInstallationEventStorage rawInstallationEventStorage;
    private final RawStorageReader rawStorageReader;
    private final RepoIndexingJobStorage repoIndexingJobStorage;
    private final UserIndexer userIndexer;
    private final RepoIndexer repoIndexer;
    private final GithubAppInstallationStorage githubAppInstallationStorage;

    @Override
    public void process(RawInstallationEvent rawEvent) {
        rawInstallationEventStorage.save(rawEvent);

        final var event = mapRawEvent(rawEvent);

        switch (event.getAction()) {
            case ADDED -> onAdded((InstallationAddedEvent) event);
            case CREATED -> onCreated((InstallationCreatedEvent) event);
            case DELETED -> onDeleted((InstallationDeletedEvent) event);
            case REMOVED -> onRemoved((InstallationRemovedEvent) event);
        }
    }

    private void onDeleted(InstallationDeletedEvent event) {
        repoIndexingJobStorage.deleteInstallation(event.getInstallationId());
        githubAppInstallationStorage.delete(event.getInstallationId());
    }

    private void onCreated(InstallationCreatedEvent event) {
        final var owner = GithubAccount.of(event.getAccount());
        final var repos = event.getRepos().stream()
                .map(repo -> GithubRepo.of(repo, owner))
                .toList();

        repoIndexingJobStorage.add(event.getInstallationId(), event.getRepos().stream()
                .map(CleanRepo::getId)
                .toArray(Long[]::new));

        githubAppInstallationStorage.save(GithubAppInstallation.of(event, owner, repos));
    }

    private void onAdded(InstallationAddedEvent event) {
        repoIndexingJobStorage.add(event.getInstallationId(), event.getReposAdded().stream()
                .map(CleanRepo::getId)
                .toArray(Long[]::new));

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
        return switch (action) {
            case ADDED -> InstallationAddedEvent.of(
                    rawEvent,
                    rawEvent.getRepositoriesAdded().stream()
                            .map(this::indexRepo)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList());

            case CREATED -> InstallationCreatedEvent.of(
                    rawEvent,
                    userIndexer.indexUser(rawEvent.getInstallation().getAccount().getId())
                            .orElseThrow(() -> OnlyDustException.notFound("User not found")),
                    rawEvent.getRepositories().stream()
                            .map(this::indexRepo)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .toList());

            case REMOVED -> InstallationRemovedEvent.of(rawEvent);

            case DELETED -> InstallationDeletedEvent.of(rawEvent);
        };
    }

    private Optional<CleanRepo> indexRepo(RawRepo eventRepo) {
        return rawStorageReader.repo(eventRepo.getId()).flatMap(repo -> repoIndexer.indexRepo(repo.getId()));
    }
}
