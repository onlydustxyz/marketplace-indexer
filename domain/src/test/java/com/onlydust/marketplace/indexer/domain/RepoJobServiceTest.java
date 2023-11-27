package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
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
    private final RepoIndexer repoIndexer = mock(RepoIndexer.class);
    private final GithubAppContext githubAppContext = new GithubAppContextStub();
    private final RepoRefreshJobService jobService = new RepoRefreshJobService(repoIndexingJobRepository, repoIndexer, githubAppContext, new RepoRefreshJobService.Config(0));

    @BeforeEach
    void setup() {
        when(repoIndexingJobRepository.installationIds()).thenReturn(Set.of(1L, 2L));
        when(repoIndexingJobRepository.reposUpdatedBefore(eq(1L), any())).thenReturn(Set.of(1L, 2L, 3L));
        when(repoIndexingJobRepository.reposUpdatedBefore(eq(2L), any())).thenReturn(Set.of(4L, 5L, 6L));
    }

    @Test
    public void should_triggers_all_jobs() {
        jobService.allJobs().forEach(Job::execute);
        for (Long repoId : Set.of(1L, 2L, 3L, 4L, 5L, 6L)) {
            verify(repoIndexer).indexRepo(repoId);
            verify(repoIndexingJobRepository).startJob(repoId);
            verify(repoIndexingJobRepository).endJob(repoId);
        }
    }
}
