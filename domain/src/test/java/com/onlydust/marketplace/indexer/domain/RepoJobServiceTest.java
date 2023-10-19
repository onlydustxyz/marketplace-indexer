package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.ports.out.RepoRefresher;
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
    private final RepoRefresher repoRefresher = mock(RepoRefresher.class);
    private final RepoRefreshJobService jobService = new RepoRefreshJobService(repoIndexingJobRepositoryStub, repoRefresher);

    @BeforeEach
    void setup() {
        repoIndexingJobRepositoryStub.feedWith(Map.of(
                1L, Set.of(1L, 2L, 3L),
                2L, Set.of(4L, 5L, 6L)
        ));
    }

    @Test
    public void should_triggers_all_jobs() {
        jobService.runAllJobs();
        verify(repoRefresher).refreshRepos(1L, Set.of(1L, 2L, 3L));
        verify(repoRefresher).refreshRepos(2L, Set.of(4L, 5L, 6L));
    }
}
