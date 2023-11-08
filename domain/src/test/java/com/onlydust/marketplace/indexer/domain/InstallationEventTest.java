package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.events.InstallationEventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.services.events.InstallationEventProcessorService;
import com.onlydust.marketplace.indexer.domain.services.indexers.RepoIndexingService;
import com.onlydust.marketplace.indexer.domain.services.indexers.UserIndexingService;
import com.onlydust.marketplace.indexer.domain.stubs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InstallationEventTest {
    final RawAccount onlyDust = RawStorageWriterStub.load("/github/users/onlyDust.json", RawAccount.class);
    final RawRepo marketplaceFrontend = RawStorageWriterStub.load("/github/repos/marketplace-frontend.json", RawRepo.class);
    private final RawInstallationEventStorageStub rawInstallationEventRepositoryStub = new RawInstallationEventStorageStub();
    private final RawInstallationEvent newInstallationEvent = RawStorageWriterStub.load("/github/events/new_installation.json", RawInstallationEvent.class);
    private final RawStorageWriterStub rawStorageRepositoryStub = new RawStorageWriterStub();
    final UserIndexer userIndexer = new UserIndexingService(rawStorageRepositoryStub);
    final RepoIndexer repoIndexer = new RepoIndexingService(rawStorageRepositoryStub, userIndexer);
    private final GithubRepoStorageStub githubRepoRepositoryStub = new GithubRepoStorageStub();
    private final RepoIndexingJobStorageStub repoIndexingJobRepositoryStub = new RepoIndexingJobStorageStub();
    private final InstallationStorageStub installationEventRepositoryStub = new InstallationStorageStub();
    private final InstallationEventHandler eventProcessorService = new InstallationEventProcessorService(
            rawInstallationEventRepositoryStub, rawStorageRepositoryStub, githubRepoRepositoryStub, repoIndexingJobRepositoryStub, userIndexer, repoIndexer, installationEventRepositoryStub);

    @BeforeEach
    void setup() {
        rawStorageRepositoryStub.feedWith(marketplaceFrontend);
        rawStorageRepositoryStub.feedWith(onlyDust);
    }

    @Test
    public void should_store_raw_events() {
        eventProcessorService.process(newInstallationEvent);

        assertCachedEventsAre(newInstallationEvent);

        final var repos = githubRepoRepositoryStub.repos();
        assertThat(repos).hasSize(1);
        final var repo = repos.get(0);
        assertThat(repo.getId()).isEqualTo(marketplaceFrontend.getId());

        final var events = installationEventRepositoryStub.installations();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getId()).isEqualTo(newInstallationEvent.getInstallation().getId());
        assertThat(events.get(0).getAccount().getId()).isEqualTo(onlyDust.getId());

        assertThat(repoIndexingJobRepositoryStub.installationIds()).containsExactly(newInstallationEvent.getInstallation().getId());
        assertThat(repoIndexingJobRepositoryStub.repos(newInstallationEvent.getInstallation().getId())).containsExactly(marketplaceFrontend.getId());
    }

    private void assertCachedEventsAre(RawInstallationEvent... events) {
        assertThat(rawInstallationEventRepositoryStub.events()).containsExactly(events);
    }
}
