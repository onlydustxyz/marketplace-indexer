package com.onlydust.marketplace.indexer.domain.services;

import com.onlydust.marketplace.indexer.domain.models.clean.CleanAccount;
import com.onlydust.marketplace.indexer.domain.models.clean.CleanRepo;
import com.onlydust.marketplace.indexer.domain.models.clean.InstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubAccount;
import com.onlydust.marketplace.indexer.domain.models.exposition.GithubRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.ports.out.GithubRepoRepository;
import com.onlydust.marketplace.indexer.domain.ports.out.RawInstallationEventRepository;
import com.onlydust.marketplace.indexer.domain.ports.out.RawStorageReader;
import com.onlydust.marketplace.indexer.domain.ports.out.RepoIndexingJobRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@AllArgsConstructor
@Slf4j
public class InstallationEventProcessorService {
    private final RawInstallationEventRepository rawInstallationEventRepository;
    private final RawStorageReader rawStorageReader;
    private final GithubRepoRepository githubRepoRepository;
    private final RepoIndexingJobRepository repoIndexingJobRepository;

    public void process(RawInstallationEvent rawEvent) {
        rawInstallationEventRepository.save(rawEvent);

        final var event = mapRawEvent(rawEvent);
        switch (event.getAction()) {
            case CREATED -> onCreated(event);
            case DELETED -> onDeleted(event);
        }
    }

    private void onDeleted(InstallationEvent event) {
        githubRepoRepository.deleteAll(event.getRepos().stream()
                .map(CleanRepo::getId)
                .toList());
        repoIndexingJobRepository.deleteAll(event.getInstallationId());
    }

    private void onCreated(InstallationEvent event) {
        final var owner = GithubAccount.of(event);
        githubRepoRepository.saveAll(event.getRepos().stream()
                .map(repo -> GithubRepo.of(repo, owner))
                .toList());
        repoIndexingJobRepository.add(event.getInstallationId(), event.getRepos().stream()
                .map(CleanRepo::getId)
                .toArray(Long[]::new));
    }

    private InstallationEvent mapRawEvent(RawInstallationEvent rawEvent) {
        final var account = CleanAccount.of(rawEvent.getInstallation().getAccount());
        final var repos = rawEvent.getRepositories().stream()
                .map(this::tryReadRepo)
                .filter(Objects::nonNull)
                .map(repo -> CleanRepo.of(repo, account))
                .toList();

        return InstallationEvent.of(rawEvent, account, repos);
    }

    private RawRepo tryReadRepo(RawRepo eventRepo) {
        return rawStorageReader.repo(eventRepo.getId()).orElse(null);
    }
}
