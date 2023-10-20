package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.RepoIndexer;
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
    private final RepoIndexer repoIndexer = mock(RepoIndexer.class);
    private final RepoRefreshJobService jobService = new RepoRefreshJobService(repoIndexingJobRepositoryStub, repoIndexer);

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
        verify(repoIndexer).indexRepo(1L);
        verify(repoIndexer).indexRepo(2L);
        verify(repoIndexer).indexRepo(3L);
        verify(repoIndexer).indexRepo(4L);
        verify(repoIndexer).indexRepo(5L);
        verify(repoIndexer).indexRepo(6L);
    }
}
