package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.UserIndexingJob;
import com.onlydust.marketplace.indexer.domain.models.UserIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.services.UserRefreshJobService;
import com.onlydust.marketplace.indexer.domain.stubs.JobSchedulerStub;
import com.onlydust.marketplace.indexer.domain.stubs.UserIndexingJobTriggerRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class UserJobServiceTest {
    private final UserIndexingJobTriggerRepositoryStub userIndexingJobTriggerRepositoryStub = new UserIndexingJobTriggerRepositoryStub();
    private final JobSchedulerStub<UserIndexingJob> userIndexingJobSchedulerStub = new JobSchedulerStub<>();
    private final UserRefreshJobService jobService = new UserRefreshJobService(userIndexingJobTriggerRepositoryStub, userIndexingJobSchedulerStub);

    @BeforeEach
    void setup() {
        userIndexingJobTriggerRepositoryStub.feedWith(
                new UserIndexingJobTrigger(1L),
                new UserIndexingJobTrigger(2L),
                new UserIndexingJobTrigger(3L),
                new UserIndexingJobTrigger(4L)
        );
    }

    @Test
    public void should_triggers_all_jobs() {
        jobService.scheduleAllJobs();
        assertScheduledUserJobsAre(
                UserIndexingJob.builder().users(Set.of(1L, 2L, 3L, 4L)).build()
        );
    }

    private void assertScheduledUserJobsAre(UserIndexingJob... jobs) {
        assertThat(userIndexingJobSchedulerStub.jobs()).containsExactly(jobs);
    }
}
