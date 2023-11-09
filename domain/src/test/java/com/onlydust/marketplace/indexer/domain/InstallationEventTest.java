package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.ports.in.events.InstallationEventHandler;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.services.events.InstallationEventProcessorService;
import com.onlydust.marketplace.indexer.domain.services.indexers.RepoIndexingService;
import com.onlydust.marketplace.indexer.domain.services.indexers.UserIndexingService;
import com.onlydust.marketplace.indexer.domain.stubs.GithubRepoStorageStub;
import com.onlydust.marketplace.indexer.domain.stubs.InstallationStorageStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawInstallationEventStorageStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InstallationEventTest {
    final RawAccount onlyDust = RawStorageWriterStub.load("/github/users/onlyDust.json", RawAccount.class);
    final RawRepo marketplaceFrontend = RawStorageWriterStub.load("/github/repos/marketplace-frontend.json", RawRepo.class);
    final GithubRepoStorageStub githubRepoRepositoryStub = new GithubRepoStorageStub();
    final RepoIndexingJobStorage repoIndexingJobRepository = mock(RepoIndexingJobStorage.class);
    final InstallationStorageStub installationEventRepositoryStub = new InstallationStorageStub();
    private final RawInstallationEventStorageStub rawInstallationEventRepositoryStub = new RawInstallationEventStorageStub();
    private final RawInstallationEvent newInstallationEvent = RawStorageWriterStub.load("/github/events/new_installation.json", RawInstallationEvent.class);
    private final RawStorageWriterStub rawStorageRepositoryStub = new RawStorageWriterStub();
    final UserIndexer userIndexer = new UserIndexingService(rawStorageRepositoryStub);
    final RepoIndexer repoIndexer = new RepoIndexingService(rawStorageRepositoryStub, userIndexer);
    final InstallationEventHandler eventHandler = new InstallationEventProcessorService(
            rawInstallationEventRepositoryStub, rawStorageRepositoryStub, repoIndexingJobRepository, userIndexer, repoIndexer, installationEventRepositoryStub);

    @BeforeEach
    void setup() {
        rawStorageRepositoryStub.feedWith(marketplaceFrontend);
        rawStorageRepositoryStub.feedWith(onlyDust);
    }

    @Test
    public void should_store_raw_events() {
        eventHandler.process(newInstallationEvent);

        assertCachedEventsAre(newInstallationEvent);

        final var events = installationEventRepositoryStub.installations();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getId()).isEqualTo(newInstallationEvent.getInstallation().getId());
        assertThat(events.get(0).getAccount().getId()).isEqualTo(onlyDust.getId());
        assertThat(events.get(0).getRepos()).hasSize(1);
        assertThat(events.get(0).getRepos().get(0).getId()).isEqualTo(marketplaceFrontend.getId());
        verify(repoIndexingJobRepository).add(newInstallationEvent.getInstallation().getId(), marketplaceFrontend.getId());
    }

    private void assertCachedEventsAre(RawInstallationEvent... events) {
        assertThat(rawInstallationEventRepositoryStub.events()).containsExactly(events);
    }
}
