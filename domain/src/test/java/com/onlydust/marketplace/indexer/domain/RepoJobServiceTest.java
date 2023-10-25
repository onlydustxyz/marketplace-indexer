package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.FullRepoIndexer;
import com.onlydust.marketplace.indexer.domain.services.RepoRefreshJobService;
import com.onlydust.marketplace.indexer.domain.stubs.RepoIndexingJobRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class RepoJobServiceTest {
    private final RepoIndexingJobRepositoryStub repoIndexingJobRepositoryStub = new RepoIndexingJobRepositoryStub();
    private final FullRepoIndexer fullRepoIndexer = mock(FullRepoIndexer.class);
    private final RepoRefreshJobService jobService = new RepoRefreshJobService(repoIndexingJobRepositoryStub, fullRepoIndexer);

    @BeforeEach
    void setup() {
        repoIndexingJobRepositoryStub.feedWith(Map.of(
                1L, Set.of(1L, 2L, 3L),
                2L, Set.of(4L, 5L, 6L)
        ));
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
