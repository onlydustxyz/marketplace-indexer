package com.onlydust.marketplace.indexer.domain.services.events;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.events.InstallationEventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubAppInstallationStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.exposition.GithubRepoStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawInstallationEventStorage;
import com.onlydust.marketplace.indexer.domain.ports.out.raw.RawStorageReader;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Transactional
public class InstallationEventProcessorService implements InstallationEventHandler {
    private final RawInstallationEventStorage rawInstallationEventStorage;
    private final RawStorageReader rawStorageReader;
    private final GithubRepoStorage githubRepoStorage;
    private final RepoIndexingJobStorage repoIndexingJobStorage;
    private final UserIndexer userIndexer;
    private final RepoIndexer repoIndexer;
    private final GithubAppInstallationStorage githubAppInstallationStorage;

    @Override
    public void process(RawInstallationEvent rawEvent) {
        rawInstallationEventStorage.save(rawEvent);

        final var event = mapRawEvent(rawEvent);
        switch (event.getAction()) {
            case CREATED -> onCreated(event);
            case DELETED -> onDeleted(event);
        }
    }

    private void onDeleted(InstallationEvent event) {
        repoIndexingJobStorage.deleteAll(event.getInstallationId());
        githubAppInstallationStorage.delete(event.getInstallationId());
    }

    private void onCreated(InstallationEvent event) {
        final var owner = GithubAccount.of(event.getAccount());
        githubRepoStorage.saveAll(event.getRepos().stream()
                .map(repo -> GithubRepo.of(repo, owner))
                .toList());
        repoIndexingJobStorage.add(event.getInstallationId(), event.getRepos().stream()
                .map(CleanRepo::getId)
                .toArray(Long[]::new));
        githubAppInstallationStorage.save(GithubAppInstallation.of(event, owner));
    }

    private InstallationEvent mapRawEvent(RawInstallationEvent rawEvent) {
        final var account = userIndexer.indexUser(rawEvent.getInstallation().getAccount().getId())
                .orElseThrow(() -> OnlyDustException.notFound("User not found"));

        final var repos = rawEvent.getRepositories().stream()
                .map(rawRepo -> tryReadRepo(rawRepo).flatMap(repo -> repoIndexer.indexRepo(repo.getId())).orElse(null))
                .filter(Objects::nonNull)
                .toList();

        return InstallationEvent.of(rawEvent, account, repos);
    }

    private Optional<RawRepo> tryReadRepo(RawRepo eventRepo) {
        return rawStorageReader.repo(eventRepo.getId());
    }
}
