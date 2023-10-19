package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.ports.out.UserRefresher;
import com.onlydust.marketplace.indexer.domain.services.UserRefreshJobService;
import com.onlydust.marketplace.indexer.domain.stubs.UserIndexingJobRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class UserJobServiceTest {
    private final UserIndexingJobRepositoryStub userIndexingJobRepositoryStub = new UserIndexingJobRepositoryStub();
    private final UserRefresher userRefresherStub = mock(UserRefresher.class);
    private final UserRefreshJobService jobService = new UserRefreshJobService(userIndexingJobRepositoryStub, userRefresherStub);

    @BeforeEach
    void setup() {
        userIndexingJobRepositoryStub.feedWith(1L, 2L, 3L, 4L);
    }

    @Test
    public void should_triggers_all_jobs() {
        jobService.runAllJobs();
        verify(userRefresherStub).refreshUsers(Set.of(1L, 2L, 3L, 4L));
    }
}
