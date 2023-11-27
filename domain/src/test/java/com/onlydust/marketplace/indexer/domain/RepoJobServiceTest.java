package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.RepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.services.jobs.RepoRefreshJobService;
import com.onlydust.marketplace.indexer.domain.stubs.GithubAppContextStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.*;

public class RepoJobServiceTest {
    private final RepoIndexingJobStorage repoIndexingJobRepository = mock(RepoIndexingJobStorage.class);
    private final RepoIndexer fullRepoIndexer = mock(RepoIndexer.class);
    private final RepoIndexer lightRepoIndexer = mock(RepoIndexer.class);
    private final GithubAppContext githubAppContext = new GithubAppContextStub();
    private final RepoRefreshJobService jobService = new RepoRefreshJobService(repoIndexingJobRepository, fullRepoIndexer, lightRepoIndexer, githubAppContext, new RepoRefreshJobService.Config(0));

    @BeforeEach
    void setup() {
        when(repoIndexingJobRepository.installationIds()).thenReturn(Set.of(1L, 2L));
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
    }

    @Test
    public void should_triggers_all_jobs() {
        jobService.allJobs().forEach(Job::execute);
        for (Long repoId : Set.of(1L, 3L, 4L, 6L)) {
            verify(fullRepoIndexer).indexRepo(repoId);
            verify(repoIndexingJobRepository).startJob(repoId);
            verify(repoIndexingJobRepository).endJob(repoId);
        }
        for (Long repoId : Set.of(2L, 5L)) {
            verify(lightRepoIndexer).indexRepo(repoId);
            verify(repoIndexingJobRepository).startJob(repoId);
            verify(repoIndexingJobRepository).endJob(repoId);
        }
    }
}
