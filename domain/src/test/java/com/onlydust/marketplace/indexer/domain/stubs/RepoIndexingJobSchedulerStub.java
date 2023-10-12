package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.RepoIndexingJob;
import com.onlydust.marketplace.indexer.domain.ports.out.JobScheduler;

import java.util.ArrayList;
import java.util.List;

public class RepoIndexingJobSchedulerStub implements JobScheduler<RepoIndexingJob> {
    private final List<RepoIndexingJob> jobs = new ArrayList<>();

    @Override
    public void scheduleJob(RepoIndexingJob job) {
        jobs.add(job);
    }

    public List<RepoIndexingJob> jobs() {
        return jobs;
    }
}
