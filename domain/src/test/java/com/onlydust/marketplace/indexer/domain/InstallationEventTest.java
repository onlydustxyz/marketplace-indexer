package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.models.raw.RawAccount;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.models.raw.github_app_events.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.ports.in.events.EventHandler;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.services.events.InstallationEventProcessorService;
import com.onlydust.marketplace.indexer.domain.stubs.InstallationStorageStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageWriterStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class InstallationEventTest {
    final RawAccount onlyDust = RawStorageWriterStub.load("/github/users/onlyDust.json", RawAccount.class);
    final RawRepo marketplaceFrontend = RawStorageWriterStub.load("/github/repos/marketplace-frontend.json", RawRepo.class);
    final RepoIndexingJobStorage repoIndexingJobRepository = mock(RepoIndexingJobStorage.class);
    final InstallationStorageStub installationEventRepositoryStub = new InstallationStorageStub();

    final EventHandler<RawInstallationEvent> eventHandler = new InstallationEventProcessorService(
            repoIndexingJobRepository, installationEventRepositoryStub);
    private final RawInstallationEvent newInstallationEvent = RawStorageWriterStub.load("/github/events/new_installation.json", RawInstallationEvent.class);
    private final RawStorageWriterStub rawStorageRepositoryStub = new RawStorageWriterStub();

    @BeforeEach
    void setup() {
        rawStorageRepositoryStub.feedWith(marketplaceFrontend);
        rawStorageRepositoryStub.feedWith(onlyDust);
    }

    @Test
    public void should_store_raw_events() {
        eventHandler.process(newInstallationEvent);

        final var events = installationEventRepositoryStub.installations();
        assertThat(events).hasSize(1);
        assertThat(events.get(0).getId()).isEqualTo(newInstallationEvent.getInstallation().getId());
        assertThat(events.get(0).getAccount().getId()).isEqualTo(onlyDust.getId());
        assertThat(events.get(0).getRepos()).hasSize(1);
        assertThat(events.get(0).getRepos().get(0).getId()).isEqualTo(marketplaceFrontend.getId());
        verify(repoIndexingJobRepository).setInstallationForRepos(newInstallationEvent.getInstallation().getId(),
                Set.of(new RepoIndexingJobTrigger(marketplaceFrontend.getId(), false, true)));
    }
}
