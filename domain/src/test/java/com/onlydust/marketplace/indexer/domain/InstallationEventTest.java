package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.raw.RawInstallationEvent;
import com.onlydust.marketplace.indexer.domain.models.raw.RawRepo;
import com.onlydust.marketplace.indexer.domain.services.InstallationEventProcessorService;
import com.onlydust.marketplace.indexer.domain.stubs.GithubRepoRepositoryStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawInstallationEventRepositoryStub;
import com.onlydust.marketplace.indexer.domain.stubs.RawStorageRepositoryStub;
import com.onlydust.marketplace.indexer.domain.stubs.RepoIndexingJobRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class InstallationEventTest {
    final RawRepo marketplaceFrontend = RawStorageRepositoryStub.load("/github/repos/marketplace-frontend.json", RawRepo.class);
    private final RawInstallationEventRepositoryStub rawInstallationEventRepositoryStub = new RawInstallationEventRepositoryStub();
    private final RawInstallationEvent newInstallationEvent = RawStorageRepositoryStub.load("/github/events/new_installation.json", RawInstallationEvent.class);
    private final RawStorageRepositoryStub rawStorageRepositoryStub = new RawStorageRepositoryStub();
    private final GithubRepoRepositoryStub githubRepoRepositoryStub = new GithubRepoRepositoryStub();
    private final RepoIndexingJobRepositoryStub repoIndexingJobRepositoryStub = new RepoIndexingJobRepositoryStub();
    private final InstallationEventProcessorService eventProcessorService = new InstallationEventProcessorService(rawInstallationEventRepositoryStub, rawStorageRepositoryStub, githubRepoRepositoryStub, repoIndexingJobRepositoryStub);

    @BeforeEach
    void setup() {
        rawStorageRepositoryStub.feedWith(marketplaceFrontend);
    }

    @Test
    public void should_store_raw_events() {
        eventProcessorService.process(newInstallationEvent);

        assertCachedEventsAre(newInstallationEvent);

        final var repos = githubRepoRepositoryStub.repos();
        assertThat(repos).hasSize(1);
        final var repo = repos.get(0);
        assertThat(repo.getId()).isEqualTo(marketplaceFrontend.getId());
        assertThat(repo.getOwner().getInstallationId()).isEqualTo(newInstallationEvent.getInstallation().getId());

        assertThat(repoIndexingJobRepositoryStub.installationIds()).containsExactly(newInstallationEvent.getInstallation().getId());
        assertThat(repoIndexingJobRepositoryStub.repos(newInstallationEvent.getInstallation().getId())).containsExactly(marketplaceFrontend.getId());
    }

    private void assertCachedEventsAre(RawInstallationEvent... events) {
        assertThat(rawInstallationEventRepositoryStub.events()).containsExactly(events);
    }
}
