package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.contexts.GithubAppContext;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.RepoIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.services.jobs.RepoRefreshJobService;
import com.onlydust.marketplace.indexer.domain.stubs.GithubAppContextStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.*;

public class RepoJobServiceTest {
    private final RepoIndexingJobStorage repoIndexingJobRepository = mock(RepoIndexingJobStorage.class);
    private final FullRepoIndexer fullRepoIndexer = mock(FullRepoIndexer.class);
    private final GithubAppContext githubAppContext = new GithubAppContextStub();
    private final RepoRefreshJobService jobService = new RepoRefreshJobService(repoIndexingJobRepository, fullRepoIndexer, githubAppContext);

    @BeforeEach
    void setup() {
        when(repoIndexingJobRepository.installationIds()).thenReturn(Set.of(1L, 2L));
        when(repoIndexingJobRepository.repos(1L)).thenReturn(Set.of(1L, 2L, 3L));
        when(repoIndexingJobRepository.repos(2L)).thenReturn(Set.of(4L, 5L, 6L));
    }

    @Test
    public void should_triggers_all_jobs() {
        jobService.allJobs().forEach(Job::execute);
        verify(fullRepoIndexer).indexFullRepo(1L);
        verify(fullRepoIndexer).indexFullRepo(2L);
        verify(fullRepoIndexer).indexFullRepo(3L);
        verify(fullRepoIndexer).indexFullRepo(4L);
        verify(fullRepoIndexer).indexFullRepo(5L);
        verify(fullRepoIndexer).indexFullRepo(6L);
    }
}
