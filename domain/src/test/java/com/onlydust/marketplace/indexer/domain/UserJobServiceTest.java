package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.services.jobs.UserRefreshJobService;
import com.onlydust.marketplace.indexer.domain.stubs.UserIndexingJobStorageStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UserJobServiceTest {
    private final UserIndexingJobStorageStub userIndexingJobRepositoryStub = new UserIndexingJobStorageStub();
    private final UserIndexer userIndexer = mock(UserIndexer.class);
    private final UserRefreshJobService jobService = new UserRefreshJobService(userIndexingJobRepositoryStub, userIndexer);

    @BeforeEach
    void setup() {
        userIndexingJobRepositoryStub.feedWith(1L, 2L, 3L, 4L);
    }

    @Test
    public void should_triggers_all_jobs() {
        jobService.allJobs().forEach(Job::execute);
        verify(userIndexer).indexUser(1L);
        verify(userIndexer).indexUser(2L);
        verify(userIndexer).indexUser(3L);
        verify(userIndexer).indexUser(4L);
    }
}
