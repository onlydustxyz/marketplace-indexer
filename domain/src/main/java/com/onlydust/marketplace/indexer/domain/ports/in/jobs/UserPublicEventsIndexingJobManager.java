package com.onlydust.marketplace.indexer.domain.ports.in.jobs;

import com.onlydust.marketplace.indexer.domain.jobs.Job;

public interface UserPublicEventsIndexingJobManager {
    Job create(Long userId);

    Job refresh();

    String name();
}
