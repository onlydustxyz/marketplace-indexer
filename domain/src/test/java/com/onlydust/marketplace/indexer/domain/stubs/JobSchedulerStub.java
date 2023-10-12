package com.onlydust.marketplace.indexer.domain.stubs;

import com.onlydust.marketplace.indexer.domain.models.Job;
import com.onlydust.marketplace.indexer.domain.ports.out.JobScheduler;

import java.util.ArrayList;
import java.util.List;

public class JobSchedulerStub<J extends Job> implements JobScheduler<J> {
    private final List<J> jobs = new ArrayList<>();

    @Override
    public void scheduleJob(J job) {
        jobs.add(job);
    }

    public List<J> jobs() {
        return jobs;
    }
}
