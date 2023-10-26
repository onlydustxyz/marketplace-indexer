package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.exception.OnlyDustException;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAppInstallation;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.transaction.Transactional;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@Slf4j
@Transactional
public class InstallationEventProcessorService {
    private final RawInstallationEventRepository rawInstallationEventRepository;
    private final RawStorageReader rawStorageReader;
    private final GithubRepoRepository githubRepoRepository;
    private final RepoIndexingJobRepository repoIndexingJobRepository;
    private final UserIndexer userIndexer;
    private final RepoIndexer repoIndexer;
    private final GithubAppInstallationRepository githubAppInstallationRepository;

    public void process(RawInstallationEvent rawEvent) {
        rawInstallationEventRepository.save(rawEvent);

        final var event = mapRawEvent(rawEvent);
        switch (event.getAction()) {
            case CREATED -> onCreated(event);
            case DELETED -> onDeleted(event);
        }
    }

    private void onDeleted(InstallationEvent event) {
        repoIndexingJobRepository.deleteAll(event.getInstallationId());
        githubAppInstallationRepository.delete(event.getInstallationId());
    }

    private void onCreated(InstallationEvent event) {
        final var owner = GithubAccount.of(event.getAccount());
        githubRepoRepository.saveAll(event.getRepos().stream()
                .map(repo -> GithubRepo.of(repo, owner))
                .toList());
        repoIndexingJobRepository.add(event.getInstallationId(), event.getRepos().stream()
                .map(CleanRepo::getId)
                .toArray(Long[]::new));
        githubAppInstallationRepository.save(GithubAppInstallation.of(event, owner));
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
