package com.onlydust.marketplace.indexer.domain.ports.in.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;

import java.util.Set;

public interface NotifierJobManager {
    Set<Job> allJobs();
}
