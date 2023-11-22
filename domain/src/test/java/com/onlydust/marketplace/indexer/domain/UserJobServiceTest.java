package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.jobs.Job;
import com.onlydust.marketplace.indexer.domain.ports.in.indexers.UserIndexer;
import com.onlydust.marketplace.indexer.domain.ports.out.jobs.UserIndexingJobStorage;
import com.onlydust.marketplace.indexer.domain.services.jobs.UserRefreshJobService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.mockito.Mockito.*;

public class UserJobServiceTest {
    private final UserIndexingJobStorage userIndexingJobRepository = mock(UserIndexingJobStorage.class);
    private final UserIndexer userIndexer = mock(UserIndexer.class);
    private final UserRefreshJobService jobService = new UserRefreshJobService(userIndexingJobRepository, userIndexer, new UserRefreshJobService.Config(0));

    @BeforeEach
    void setup() {
        when(userIndexingJobRepository.usersUpdatedBefore(any())).thenReturn(Set.of(1L, 2L, 3L, 4L));
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
