package com.onlydust.marketplace.indexer.domain;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJob;
import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJobTrigger;
import com.onlydust.marketplace.indexer.domain.services.RepoIndexingJobService;
import com.onlydust.marketplace.indexer.domain.stubs.RepoIndexingJobSchedulerStub;
import com.onlydust.marketplace.indexer.domain.stubs.RepoIndexingJobTriggerRepositoryStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class JobServiceTest {
    private final RepoIndexingJobTriggerRepositoryStub repoIndexingJobTriggerRepositoryStub = new RepoIndexingJobTriggerRepositoryStub();
    private final RepoIndexingJobSchedulerStub repoIndexingJobSchedulerStub = new RepoIndexingJobSchedulerStub();
    private final RepoIndexingJobService jobService = new RepoIndexingJobService(repoIndexingJobTriggerRepositoryStub, repoIndexingJobSchedulerStub);

    @BeforeEach
    void setup() {
        repoIndexingJobTriggerRepositoryStub.feedWith(
                new RepoIndexingJobTrigger(1L, 1L),
                new RepoIndexingJobTrigger(1L, 2L),
                new RepoIndexingJobTrigger(1L, 3L),
                new RepoIndexingJobTrigger(2L, 4L),
                new RepoIndexingJobTrigger(2L, 5L),
                new RepoIndexingJobTrigger(2L, 6L)
        );
    }

    @Test
    public void should_list_all_triggers() {
        jobService.scheduleAllJobs();
        assertScheduledJobsAre(
                RepoIndexingJob.builder().installationId(1L).repos(Set.of(1L, 2L, 3L)).build(),
                RepoIndexingJob.builder().installationId(2L).repos(Set.of(4L, 5L, 6L)).build()
        );
    }

    private void assertScheduledJobsAre(RepoIndexingJob... jobs) {
        assertThat(repoIndexingJobSchedulerStub.jobs()).containsExactly(jobs);
    }
}
