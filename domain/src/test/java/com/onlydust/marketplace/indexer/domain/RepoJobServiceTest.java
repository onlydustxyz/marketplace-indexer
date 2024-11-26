package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.services.jobs.RepoRefreshJobService;
import com.onlydust.marketplace.indexer.domain.stubs.GithubAppContextStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

public class RepoJobServiceTest {
    private final RepoIndexingJobStorage repoIndexingJobRepository = mock(RepoIndexingJobStorage.class);
    private final RepoIndexer fullRepoIndexer = mock(RepoIndexer.class);
    private final RepoIndexer lightRepoIndexer = mock(RepoIndexer.class);
    private final GithubAppContext githubAppContext = new GithubAppContextStub();
    private final Executor executor = Runnable::run;
    private final RepoRefreshJobService jobService = new RepoRefreshJobService(executor, repoIndexingJobRepository, fullRepoIndexer, lightRepoIndexer,
            githubAppContext, new RepoRefreshJobService.Config(0, 2, 0));

    @BeforeEach
    void setup() {
        final var installations = new HashSet<Long>();
        installations.add(1L);
        installations.add(2L);
        installations.add(null);
        when(repoIndexingJobRepository.installationIds()).thenReturn(installations);
        when(repoIndexingJobRepository.reposUpdatedBefore(eq(1L), any())).thenReturn(Set.of(
                new RepoIndexingJobTrigger(1L, true, true),
                new RepoIndexingJobTrigger(2L, false, true),
                new RepoIndexingJobTrigger(3L, true, true)
        ));

        when(repoIndexingJobRepository.reposUpdatedBefore(eq(2L), any())).thenReturn(Set.of(
                new RepoIndexingJobTrigger(4L, true, true),
                new RepoIndexingJobTrigger(5L, false, true),
                new RepoIndexingJobTrigger(6L, true, true)
        ));

        when(repoIndexingJobRepository.reposUpdatedBefore(isNull(), any())).thenReturn(Set.of(
                new RepoIndexingJobTrigger(7L, false, true),
                new RepoIndexingJobTrigger(8L, false, true),
                new RepoIndexingJobTrigger(9L, false, true)
        ));
    }

    @Test
    public void should_triggers_all_jobs() {
        // When
        jobService.createJob().run();

        // Then
        for (Long repoId : Set.of(1L, 3L, 4L, 6L)) {
            verify(fullRepoIndexer).indexRepo(repoId);
            verify(repoIndexingJobRepository).startJob(repoId);
            verify(repoIndexingJobRepository).endJob(repoId);
        }

        for (Long repoId : Set.of(2L, 5L, 7L, 8L)) {
            verify(lightRepoIndexer).indexRepo(repoId);
            verify(repoIndexingJobRepository).startJob(repoId);
            verify(repoIndexingJobRepository).endJob(repoId);
        }

        for (Long repoId : Set.of(9L)) {
            verify(lightRepoIndexer, never()).indexRepo(repoId);
            verify(fullRepoIndexer, never()).indexRepo(repoId);
            verify(repoIndexingJobRepository, never()).startJob(repoId);
            verify(repoIndexingJobRepository, never()).endJob(repoId);
        }
    }
}
